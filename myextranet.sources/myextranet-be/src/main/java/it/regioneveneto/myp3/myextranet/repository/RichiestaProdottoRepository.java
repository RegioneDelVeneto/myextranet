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

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.regioneveneto.myp3.myextranet.model.Ente;
import it.regioneveneto.myp3.myextranet.model.ProdottoAttivabile;
import it.regioneveneto.myp3.myextranet.model.RichiestaProdotto;

public interface RichiestaProdottoRepository extends JpaRepository<RichiestaProdotto, Integer>, JpaSpecificationExecutor<RichiestaProdotto> {

	@Query("SELECT r.idProdAttivRich FROM RichiestaProdotto r WHERE r.prodottoAttivato.idAttivazione = :idAttivazione and r.flgFineRich = :flgFineRich")
	List<Integer> getIdProdAttivRichByIdAttivazioneAndFlgFineRich(@Param("idAttivazione") Integer idAttivazione, @Param("flgFineRich") Integer flgFineRich);

	List<RichiestaProdotto> findAllByProcedimentoProdottoIdProdottoProcAndCodStatoAndFlgFineRich(Integer idProdottoProc, String codStato, Integer flgFineRich);

	List<RichiestaProdotto> findAllByProdottoAttivatoIdAttivazioneAndFlgFineRich(Integer idAttivazione, Integer flgFineRich);
	
	List<RichiestaProdotto> findAllByProdottoAttivatoProdottoAttivabileIdProdottoAttAndProdottoAttivatoEnteIdEnteAndFlgFineRich(Integer idProdottoAtt, Integer idEnte, Integer flgFineRich);

	@Query("SELECT r.procedimentoProdotto.idProdottoProc FROM RichiestaProdotto r WHERE r.idProdAttivRich = :idProdAttivRich")
	Integer getIdProdottoProcByIdProdAttivRich(@Param("idProdAttivRich") Integer idProdAttivRich);

	@Query("SELECT r.codStato FROM RichiestaProdotto r WHERE r.idProdAttivRich = :idProdAttivRich")
	String getCodStatoByIdProdAttivRich(@Param("idProdAttivRich") Integer idProdAttivRich);

	@Query("SELECT r.prodottoAttivato.idAttivazione FROM RichiestaProdotto r WHERE r.idProdAttivRich = :idProdAttivRich")
	Integer getIdAttivazioneByIdProdAttivRich(@Param("idProdAttivRich") Integer idProdAttivRich);
	
	@Query("SELECT r.prodottoAttivato.idAttivazione FROM RichiestaProdotto r WHERE r.flgFineRich = :flgFineRich")
	List<Integer> getIdAttivazioneByFlgFineRich(@Param("flgFineRich") Integer flgFineRich);
	
	@Query("SELECT r.procedimentoProdotto.tipoRichiestaProdotto.flgAttivaProdotto FROM RichiestaProdotto r WHERE r.idProdAttivRich = :idProdAttivRich")
	Integer getFlgAttivaProdottoByIdProdAttivRich(@Param("idProdAttivRich") Integer idProdAttivRich);

	@Query("SELECT r.prodottoAttivato.ente FROM RichiestaProdotto r WHERE r.idProdAttivRich = :idProdAttivRich")	
	Ente getEnteByIdProdAttivRich(@Param("idProdAttivRich") Integer idProdAttivRich);
	
	@Query("SELECT r.prodottoAttivato.prodottoAttivabile FROM RichiestaProdotto r WHERE r.idProdAttivRich = :idProdAttivRich")	
	ProdottoAttivabile getProdottoAttivabileByIdProdAttivRich(@Param("idProdAttivRich") Integer idProdAttivRich);

	Integer countAllByProdottoAttivatoIdAttivazioneAndFlgFineRich(Integer idAttivazione, Integer flgFineRich);

	@Query(value = "SELECT distinct\n"
			+ "	pa.id_prodotto_att\n"
			+ "FROM \n"
			+ "	myext_prodotto_rich r\n"
			+ "	join myext_prodotto_attivato pa on r.id_attivazione = pa.id_attivazione\n"
			+ "WHERE\n"
			+ "	r.flg_fine_rich = 0\n"
			+ "	and pa.id_ente = :idEnte", nativeQuery = true)
	List<Integer> getIdProdottoAttListForOpenByIdEnte(@Param("idEnte") Integer idEnte);
}
