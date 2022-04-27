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

import it.regioneveneto.myp3.myextranet.model.Ente;
import it.regioneveneto.myp3.myextranet.model.RappresentanteEnte;
import it.regioneveneto.myp3.myextranet.model.Utente;

public interface RappresentanteEnteRepository extends JpaRepository<RappresentanteEnte, Integer>, JpaSpecificationExecutor<RappresentanteEnte> {

	@Query("SELECT r FROM RappresentanteEnte r WHERE r.tipoRappr = :tipoRappr and r.ente = :ente and r.utente = :utente and (r.flgConferma = -1 or r.flgConferma = 0 or (r.flgConferma = 1 and :refDate >= r.dtInizioVal AND :refDate < r.dtFineVal))")
	List<RappresentanteEnte> findAllCurrentByEnteAndUtenteAndTipoRappr(@Param("ente") Ente ente, @Param("utente") Utente utente, @Param("tipoRappr") String tipoRappr,
			@Param("refDate") Date valueOf);
	
	@Query("SELECT r FROM RappresentanteEnte r WHERE r.tipoRappr = :tipoRappr and r.ente.idEnte = :idEnte and r.utente.codFiscale = :codFiscale and r.flgConferma = 1 and :refDate >= r.dtInizioVal AND :refDate < r.dtFineVal")
	List<RappresentanteEnte> findAllActiveByEnteAndUtente(@Param("idEnte") Integer idEnte, @Param("codFiscale") String codFiscale, @Param("tipoRappr") String tipoRappr, @Param("refDate") Date refDate);

	@Modifying
    @Query("UPDATE RappresentanteEnte r SET r.dtFineVal = :refDate WHERE r.tipoRappr = :tipoRappr and r.ente = :ente and r.flgConferma = 1 and :refDate >= r.dtInizioVal AND :refDate < r.dtFineVal")
	void invalidateActiveRepresentatives(@Param("tipoRappr") String tipoRappr, @Param("ente") Ente ente, @Param("refDate") Date refDate);

	@Query("SELECT r FROM RappresentanteEnte r WHERE r.tipoRappr = :tipoRappr and r.ente = :ente and r.flgConferma = 1 and :refDate >= r.dtInizioVal AND :refDate < r.dtFineVal")
	List<RappresentanteEnte> findAllActiveRepresentatives(@Param("tipoRappr") String tipoRappr, @Param("ente") Ente ente, @Param("refDate") Date refDate);

	@Query("SELECT r FROM RappresentanteEnte r WHERE r.tipoRappr = :tipoRappr and r.ente = :ente and r.utente != :utente and r.flgConferma = 0 ")
	List<RappresentanteEnte> findAllOthersPendingByEnteAndUtenteAndTipoRappr(@Param("ente") Ente ente, @Param("utente") Utente utente, @Param("tipoRappr") String tipoRappr);

	
}
