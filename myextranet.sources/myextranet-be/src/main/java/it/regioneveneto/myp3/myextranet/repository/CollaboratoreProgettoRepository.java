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

import it.regioneveneto.myp3.myextranet.bean.ICounterPerEntity;
import it.regioneveneto.myp3.myextranet.model.CollaboratoreProgetto;
import it.regioneveneto.myp3.myextranet.model.Utente;

public interface CollaboratoreProgettoRepository extends JpaRepository<CollaboratoreProgetto, Integer>, JpaSpecificationExecutor<CollaboratoreProgetto> {
	

	List<CollaboratoreProgetto> findAllByIdProgettoOrderByUtenteCognomeAscUtenteNomeAsc(String idProgetto);
	
	@Query("SELECT c FROM CollaboratoreProgetto c WHERE c.idProgetto = :idProgetto and c.utente = :utente and (c.flgConferma = 0 or (c.flgConferma = 1 and :refDate >= c.dtInizioVal AND :refDate < c.dtFineVal))")
	List<CollaboratoreProgetto> findAllCurrentByIdProgettoAndUtente(@Param("idProgetto") String idProgetto, @Param("utente") Utente utente, @Param("refDate") Date refDate);

	@Modifying
    @Query("UPDATE CollaboratoreProgetto c SET c.flgCoord = :value WHERE c.idCollab IN :ids")
	int setFlgCoordForIn(@Param("value") Integer value, @Param("ids") Integer[] ids);
	
	@Query("SELECT c FROM CollaboratoreProgetto c WHERE c.idProgetto = :idProgetto AND c.utente.codFiscale = :codiceFiscale AND c.flgConferma = 1 AND :refDate >= c.dtInizioVal AND :refDate < c.dtFineVal")
	CollaboratoreProgetto findActiveCollaboration(@Param("idProgetto") String idProgetto, @Param("codiceFiscale") String codiceFiscale, @Param("refDate") Date refDate);

	@Query("SELECT c.idProgetto FROM CollaboratoreProgetto c WHERE c.utente.codFiscale = :codiceFiscale AND c.flgConferma = 1 AND :refDate >= c.dtInizioVal AND :refDate < c.dtFineVal")
	String[] getProjectIdsForActiveCollaborations(@Param("codiceFiscale") String codiceFiscale, @Param("refDate") Date refDate);

	List<CollaboratoreProgetto> findAllByFlgConfermaAndUtenteAndIdProgetto(Integer flgConferma, Utente utente, String idProgetto);

	@Query(value = "SELECT c \n"
			+ "FROM CollaboratoreProgetto c \n"
			+ "WHERE\n"
			+ "c.utente = :utente\n"
			+ "and c.idProgetto = :idProgetto\n"
			+ "and \n"
			+ "(\n"
			+ "(c.flgConferma = 1 and c.dtFineVal < :refDate) or c.flgConferma = 3 or c.flgConferma = 4\n"
			+ ") order by c.idCollab asc", nativeQuery = false)
	List<CollaboratoreProgetto> findPastCollaborations(@Param("utente") Utente utente, @Param("idProgetto") String idProgetto, @Param("refDate") Date refDate);


	@Query(value = "SELECT id_progetto as id, count(flg_conferma) FROM myext_progetto_collab where flg_conferma = 0 group by id_progetto;", nativeQuery = true)
	List<ICounterPerEntity> getCountersForProjectsWithPendingRequests();

	@Query(value = "SELECT id_progetto as id, count(flg_conferma) FROM myext_progetto_collab where id_progetto in (:ids) and flg_conferma = 0 group by id_progetto;", nativeQuery = true)
	List<ICounterPerEntity> getCountersForProjectsWithPendingRequestsInList(@Param("ids") String[] ids);

}
