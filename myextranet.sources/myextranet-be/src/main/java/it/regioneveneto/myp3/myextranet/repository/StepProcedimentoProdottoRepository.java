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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.regioneveneto.myp3.myextranet.model.ProcedimentoProdotto;
import it.regioneveneto.myp3.myextranet.model.StepProcedimentoProdotto;

public interface StepProcedimentoProdottoRepository extends JpaRepository<StepProcedimentoProdotto, Integer>, JpaSpecificationExecutor<StepProcedimentoProdotto> {

	@Query("SELECT count(s) FROM StepProcedimentoProdotto s WHERE s.codStato = :codStato and s.procedimentoProdotto.idProdottoProc = :idProdottoProc")
	int countConflictingRecords(@Param("codStato") String codStato, @Param("idProdottoProc") Integer idProdottoProc);

	@Query("SELECT count(s) FROM StepProcedimentoProdotto s WHERE s.codStato = :codStato and s.procedimentoProdotto.idProdottoProc = :idProdottoProc and s.idStatoConf != :idStatoConf")
	int countConflictingRecords(@Param("codStato") String codStato, @Param("idProdottoProc") Integer idProdottoProc, @Param("idStatoConf") Integer idStatoConf);

	List<StepProcedimentoProdotto> findAllByProcedimentoProdottoIdProdottoProcOrderByNumStep(Integer idProdottoProc);

	Optional<StepProcedimentoProdotto> findOneByProcedimentoProdottoAndNumStep(ProcedimentoProdotto procedimentoProdotto,
			Integer integer);
	
	Optional<StepProcedimentoProdotto> findOneByProcedimentoProdottoIdProdottoProcAndCodStato(Integer idProdottoProc,
			String codStato);

	@Query("SELECT s.codStato FROM StepProcedimentoProdotto s WHERE s.procedimentoProdotto.idProdottoProc = :idProdottoProc and s.competenza = :competenza order by s.numStep asc")
	List<String> getCodStatoByIdProdottoProcAndCompetenza(@Param("idProdottoProc") Integer idProdottoProc, @Param("competenza") String competenza);

	@Query("SELECT s.numStep FROM StepProcedimentoProdotto s WHERE s.procedimentoProdotto.idProdottoProc = :idProdottoProc and s.codStato = :codStato")
	Integer getNumStepByProcedimentoProdottoIdProdottoProcAndCodStato(@Param("idProdottoProc") Integer idProdottoProc, @Param("codStato") String codStato);

	Optional<StepProcedimentoProdotto> findFirstByProcedimentoProdottoIdProdottoProcAndFlgFineRichOrderByNumStepDesc(
			Integer idProdottoProc, Integer flgFineRich);

	Optional<StepProcedimentoProdotto> findOneByProcedimentoProdottoIdProdottoProcAndNumStep(Integer idProdottoProc, Integer numStep);

	@Query("SELECT s FROM StepProcedimentoProdotto s WHERE s.procedimentoProdotto.idProdottoProc = :idProdottoProc and not s.flgFineRich = :flgFineRich order by s.numStep asc")
	List<StepProcedimentoProdotto> getAllByProcedimentoProdottoIdProdottoProcAndNotFlgFineRichOrderByNumStepAsc(@Param("idProdottoProc") Integer idProdottoProc, @Param("flgFineRich") Integer flgFineRich);

	void deleteAllByProcedimentoProdotto(ProcedimentoProdotto originalProcedimentoProdotto);
	
}
