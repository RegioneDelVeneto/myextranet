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

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.regioneveneto.myp3.myextranet.model.ProcedimentoProdotto;

public interface ProcedimentoProdottoRepository extends JpaRepository<ProcedimentoProdotto, Integer>, JpaSpecificationExecutor<ProcedimentoProdotto> {

	@Query("SELECT count(p) FROM ProcedimentoProdotto p WHERE p.prodotto.idProdottoAtt = :idProdottoAtt and p.tipoRichiestaProdotto.codTipoRich = :codTipoRich")
	int countConflictingRecords(@Param("idProdottoAtt") Integer idProdottoAtt, @Param("codTipoRich") String codTipoRich);

	@Query("SELECT count(p) FROM ProcedimentoProdotto p WHERE p.prodotto.idProdottoAtt = :idProdottoAtt and p.tipoRichiestaProdotto.codTipoRich = :codTipoRich and p.idProdottoProc != :idProdottoProc")
	int countConflictingRecords(@Param("idProdottoAtt") Integer idProdottoAtt, @Param("codTipoRich") String codTipoRich, @Param("idProdottoProc") Integer idProdottoProc);
	
	Optional<ProcedimentoProdotto> findOneByProdottoIdProdottoAttAndTipoRichiestaProdottoCodTipoRichAndFlgEnabled(Integer idProdottoAtt, String codTipoRich, Integer flgEnabled);

	@Query("SELECT p.idProdottoProc FROM ProcedimentoProdotto p WHERE p.prodotto.idProdottoAtt = :idProdottoAtt and p.tipoRichiestaProdotto.codTipoRich = :codTipoRich")
	Integer getIdProdottoProcByIdProdottoAttAndCodTipoRich(@Param("idProdottoAtt") Integer idProdottoAtt, @Param("codTipoRich") String codTipoRich);

	@Query("SELECT p.tipoRichiestaProdotto.codTipoRich FROM ProcedimentoProdotto p WHERE p.idProdottoProc = :idProdottoProc")
	String getCodTipoRichByIdProdottoProc(@Param("idProdottoProc") Integer idProdottoProc);
}
