/**
 *     MyExtranet, il portale per collaborare con l’ente Regione Veneto.
 *     Copyright (C) 2022  Regione Veneto
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package it.regioneveneto.myp3.myextranet.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.regioneveneto.myp3.myextranet.bean.IStatNomeValoreRow;
import it.regioneveneto.myp3.myextranet.bean.IStatProdottiRow;
import it.regioneveneto.myp3.myextranet.model.ProdottoAttivabile;

public interface ProdottoAttivabileRepository extends JpaRepository<ProdottoAttivabile, Integer>, JpaSpecificationExecutor<ProdottoAttivabile> {
	
	List<ProdottoAttivabile> findAllByOrderByDtAttivabileDaAscDtAttivabileAAsc();
	
	Optional<ProdottoAttivabile> findOneByIdProdotto(String idProdotto);
	
	@Query(value = "select\n"
			+ "t.nome_prodotto_attiv as prodotto,\n"
			+ "	count(distinct t.attiv_valida) as numEntiServiti,\n"
			+ "	count(t.utente_valido) as numOperatoriCoinvolti\n"
			+ "from (\n"
			+ "\n"
			+ "\n"
			+ "SELECT \n"
			+ "	pae.nome_prodotto_attiv,\n"
			+ "	pao.id_attivazione,\n"
			+ "	pau.cod_fiscale,\n"
			+ "	CASE    \n"
			+ "		WHEN pao.dt_inizio_val <= now() and pao.dt_fine_val > now() THEN pao.id_attivazione          \n"
			+ "		ELSE null    \n"
			+ "	END as attiv_valida,\n"
			+ "	CASE    \n"
			+ "		WHEN pao.dt_inizio_val <= now() and pao.dt_fine_val > now() and pau.cod_fiscale is not null and pau.flg_enabled = 1 THEN 1             \n"
			+ "		ELSE null    \n"
			+ "	END as utente_valido\n"
			+ "\n"
			+ "	FROM public.myext_prodotto_attivabile pae\n"
			+ "	left join\n"
			+ "        myext_prodotto_attivato pao\n"
			+ "            on pae.id_prodotto_att = pao.id_prodotto_att    \n"
			+ "	left join\n"
			+ "        myext_prodotto_attiv_utenti pau\n"
			+ "            on pau.id_attivazione = pao.id_attivazione    \n"
			+ "	\n"
			+ ") as t	\n"
			+ "	\n"
			+ "	 group by t.nome_prodotto_attiv\n"
			+ "	;", nativeQuery = true)
	List<IStatProdottiRow> getStatProdottiRows();
		
	@Query(value = "SELECT \n"
			+ "	'ProdottiErogati'\\:\\:varchar as nome,\n"
			+ "	count(*) as valore\n"
			+ "FROM \n"
			+ "	myext_prodotto_attivabile p\n"
			+ "WHERE \n"
			+ "	p.dt_attivabile_da <= now() and p.dt_attivabile_a > now()\n"
			+ "", nativeQuery = true)
	IStatNomeValoreRow getNumProdottiErogati();
	
}
