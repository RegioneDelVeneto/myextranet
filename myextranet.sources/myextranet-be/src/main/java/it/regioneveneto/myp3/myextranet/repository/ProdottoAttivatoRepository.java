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

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.regioneveneto.myp3.myextranet.bean.IStatEntiProdottiRow;
import it.regioneveneto.myp3.myextranet.bean.IStatNomeValoreRow;
import it.regioneveneto.myp3.myextranet.bean.IStatServiziErogatiRow;
import it.regioneveneto.myp3.myextranet.model.Ente;
import it.regioneveneto.myp3.myextranet.model.ProdottoAttivabile;
import it.regioneveneto.myp3.myextranet.model.ProdottoAttivato;

public interface ProdottoAttivatoRepository extends JpaRepository<ProdottoAttivato, Integer>, JpaSpecificationExecutor<ProdottoAttivato> {
	
	List<ProdottoAttivato> findAllByOrderByDtInizioValAscDtFineValAsc();
	
	List<ProdottoAttivato> findAllByEnte(Ente ente);

	@Query("SELECT p FROM ProdottoAttivato p WHERE p.prodottoAttivabile = :prodottoAttivabile and p.ente = :ente")
	List<ProdottoAttivato> findAllCurrentByProdottoAttivabileAndEnte(@Param("prodottoAttivabile") ProdottoAttivabile prodottoAttivabile, @Param("ente") Ente ente);
		
	@Query(value = "SELECT \n"
			+ "	e.denominazione as ente,\n"
			+ "	count(distinct pao.id_attivazione) as numProdottiAttivati ,\n"
			+ "	count(pau.cod_fiscale) as numOperatoriCoinvolti\n"
			+ "	FROM\n"
			+ "		myext_prodotto_attivato pao \n"
			+ "		left join myext_ente e\n"
			+ "            on e.id_ente = pao.id_ente   \n"
			+ "		left join myext_prodotto_attiv_utenti pau\n"
			+ "            on pau.id_attivazione = pao.id_attivazione \n"
			+ "	where \n"
			+ "		pao.dt_inizio_val <= now() and pao.dt_fine_val > now() and pau.flg_enabled = 1	\n"
			+ "	group by e.denominazione\n"
			+ "	;", nativeQuery = true)
	List<IStatEntiProdottiRow> getStatEntiProdottiRows();
	
	@Query(value = "SELECT \n"
			+ "	ec.des_categoria as desCategoria,\n"
			+ "	pae.id_prodotto as idProdotto,\n"
			+ "	pae.id_prodotto_att as idProdottoAtt,\n"
			+ "	count (pae.id_prodotto_att) as count\n"
			+ "FROM \n"
			+ "	myext_prodotto_attivato pao\n"
			+ "	join myext_prodotto_attivabile pae on pao.id_prodotto_att = pae.id_prodotto_att\n"
			+ "	join myext_ente e on pao.id_ente = e.id_ente\n"
			+ "	join myext_ente_categoria ec on e.id_categoria = ec.id_categoria\n"
			+ "WHERE\n"
			+ "	pao.dt_inizio_val <= now() and pao.dt_fine_val > now()\n"
			+ "	and pae.id_prodotto is not null\n"
			+ " GROUP BY\n"
			+ "	ec.des_categoria, pae.id_prodotto_att\n"
			+ "ORDER BY\n"
			+ "	ec.des_categoria, pae.id_prodotto", nativeQuery = true)
	List<IStatServiziErogatiRow> getStatServiziErogatiRows();

	List<ProdottoAttivato> findAllByProdottoAttivabileIdProdottoAttAndEnteIdEnte(Integer idProdottoAtt, Integer idEnte);
	
	@Query("SELECT p FROM ProdottoAttivato p WHERE p.prodottoAttivabile.idProdottoAtt = :idProdottoAtt and p.ente.idEnte = :idEnte and :refDate >= p.dtInizioVal AND :refDate < p.dtFineVal")
	List<ProdottoAttivato> findAllValidByProdottoAttivabileIdProdottoAttAndEnteIdEnte(@Param("idProdottoAtt") Integer idProdottoAtt, @Param("idEnte") Integer idEnte, @Param("refDate") Date refDate);

	@Modifying
    @Query("UPDATE ProdottoAttivato p SET p.dtInizioVal = :start, p.dtFineVal = :end WHERE p.idAttivazione = :idAttivazione")
	void setValidityDates(@Param("idAttivazione") Integer idAttivazione, @Param("start") Date start, @Param("end") Date end);

	@Query(value = "SELECT \n"
			+ "	p.id_prodotto_att\n"
			+ "FROM \n"
			+ "	myext_prodotto_attivato p\n"
			+ "WHERE\n"
			+ "	p.id_ente = :idEnte\n"
			+ "	and p.dt_inizio_val <= now() and p.dt_fine_val > now()\n"
			+ ";", nativeQuery = true)
	List<Integer> getIdProdottoAttListForValidByIdEnte(@Param("idEnte") Integer idEnte);

	@Query(value = "SELECT \n"
			+ "	'EntiServiti'\\:\\:varchar as nome,\n"
			+ "	count(distinct p.id_ente) as valore\n"
			+ "FROM \n"
			+ "	myext_prodotto_attivato p\n"
			+ "WHERE \n"
			+ "	p.dt_inizio_val <= now() and p.dt_fine_val > now()\n"
			+ "", nativeQuery = true)
	IStatNomeValoreRow getNumEntiServiti();

	@Query(value = "select \n"
			+ "	t.id_attivazione\n"
			+ "from (\n"
			+ "SELECT \n"
			+ "p.id_attivazione,\n"
			+ "case when (r.flg_fine_rich is null or r.flg_fine_rich <> 0) then 1\n"
			+ "	else 0\n"
			+ "	end as isNotPending\n"
			+ "FROM \n"
			+ "	myext_prodotto_attivato p\n"
			+ "	left join myext_prodotto_rich r on p.id_attivazione = r.id_attivazione\n"
			+ "WHERE not (p.dt_inizio_val <= now() and p.dt_fine_val > now())\n"
			+ ") t\n"
			+ "group by t.id_attivazione\n"
			+ "having bit_and(t.isNotPending) = 1", nativeQuery = true)
	List<Integer> getIdAttivazioneNotValidWithNoPendingRequests();

	@Query(value = "SELECT distinct\n"
			+ "p.id_attivazione\n"
			+ "FROM \n"
			+ "	myext_prodotto_attivato p\n"
			+ "	join myext_prodotto_rich r on p.id_attivazione = r.id_attivazione\n"
			+ "	join myext_prodotto_proc pp on r.id_prodotto_proc = pp.id_prodotto_proc\n"
			+ "	join myext_prodotto_tipo_rich ptr on pp.cod_tipo_rich = ptr.cod_tipo_rich\n"
			+ "WHERE  \n"
			+ "	ptr.flg_attiva_prodotto = 1 and\n"
			+ "	((p.dt_inizio_val is null and p.dt_fine_val is null) or \n"
			+ " (not (p.dt_inizio_val <= now() and p.dt_fine_val > now())))\n"
			+ "	and r.flg_fine_rich = 0\n"
			+ "", nativeQuery = true)
	List<Integer> getIdAttivazioneNotValidWithPendingRequests();

	@Query(value = "SELECT \n"
			+ "p.id_attivazione\n"
			+ "FROM myext_prodotto_attivato p\n"
			+ "WHERE p.dt_inizio_val <= now() and p.dt_fine_val > now()\n"
			+ "", nativeQuery = true)
	List<Integer> getIdAttivazioneValid();

	
}
