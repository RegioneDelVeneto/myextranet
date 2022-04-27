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

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.regioneveneto.myp3.myextranet.bean.IStatEventiRow;
import it.regioneveneto.myp3.myextranet.bean.IStatPartecipantiRow;
import it.regioneveneto.myp3.myextranet.model.IscrittoEvento;
import it.regioneveneto.myp3.myextranet.model.Utente;

public interface IscrittoEventoRepository extends JpaRepository<IscrittoEvento, Integer>, JpaSpecificationExecutor<IscrittoEvento> {
	
	@Modifying
    @Query("UPDATE IscrittoEvento i SET i.flgPartecipLoco = :value WHERE i.idIscritto IN :ids")
    int setFlgPartecipLocoForIn(@Param("value") Integer value, @Param("ids") Integer[] ids);
	
	@Modifying
    @Query("UPDATE IscrittoEvento i SET i.flgRelatore = :value WHERE i.idIscritto IN :ids")
    int setFlgRelatoreForIn(@Param("value") Integer value, @Param("ids") Integer[] ids);

	@Modifying
    @Query("UPDATE IscrittoEvento i SET i.dtInvioAttestato = :value WHERE i.idIscritto = :id")
    int setDtInvioAttestato(@Param("value") LocalDate value, @Param("id") Integer id);
	
	@Modifying
    @Query("UPDATE IscrittoEvento i SET i.dtRichQuestionario = :value WHERE i.idIscritto = :id")
    int setDtRichQuestionario(@Param("value") LocalDate value, @Param("id") Integer id);
	
	List<IscrittoEvento> findAllByIdEventoOrderByCognomeAscNomeAsc(String idEvento);
	
	List<IscrittoEvento> findAllByIdEventoAndUtente(String idEvento, Utente utente);
	
	Integer countByIdEvento(String idEvento);
	
	Integer countByIdEventoAndFlgPartecipPref(String idEvento, String flgPartecipPref);

	@Query("SELECT i.idEvento FROM IscrittoEvento i WHERE i.utente.idUtente = :idUtente")
	String[] getEventIdsForSubscriptions(@Param("idUtente") Integer idUtente);
	
	
	@Query(value = "SELECT t.id_evento as evento, \n"
			+ "		count(t.iscritto) as numIscritti,\n"
			+ "		count(t.partecipante) as numPartecipanti,\n"
			+ "		count(distinct t.ente) as numEnti\n"
			+ "		\n"
			+ "		FROM\n"
			+ "\n"
			+ "(\n"
			+ "(SELECT i.id_evento, \n"
			+ " 	1 as tbl,\n"
			+ "	1 as iscritto,  \n"
			+ "	CASE WHEN i.flg_partecip_remoto=1 or i.flg_partecip_loco=1  THEN 1\n"
			+ "            ELSE null\n"
			+ "       END as partecipante,\n"
			+ "	CASE WHEN i.flg_partecip_remoto=1 or i.flg_partecip_loco=1  THEN i.id_ente\n"
			+ "            ELSE null\n"
			+ "       END as ente\n"
			+ "	\n"
			+ "	FROM myext_evento_iscritti i \n"
			+ "	\n"
			+ "where i.id_evento in (:ids)\n"
			+ ")\n"
			+ "\n"
			+ "union all\n"
			+ "\n"
			+ "(SELECT i.id_evento, \n"
			+ "    2 as tbl,\n"
			+ "	null as iscritto,  \n"
			+ "	null as partecipante,\n"
			+ "	CASE WHEN i.flg_partecip_remoto=1 or i.flg_partecip_loco=1  THEN u.id_ente\n"
			+ "            ELSE null\n"
			+ "       END as ente\n"
			+ "	\n"
			+ "	FROM myext_evento_iscritti i left join myext_utente u on u.id_utente = i.id_utente\n"
			+ "	\n"
			+ "where i.id_evento in (:ids)\n"
			+ ")\n"
			+ ") t\n"
			+ "\n"
			+ "group by t.id_evento", nativeQuery = true)
	List<IStatEventiRow> getStatEventiRows(@Param("ids") List<String> ids);
	
	@Query(value = "SELECT \n"
			+ "	i.id_evento as evento,\n"
			+ "	i.cognome || ' ' || i.nome as partecipante,\n"
			+ "	CASE \n"
			+ "		WHEN eu.denominazione is not null THEN eu.denominazione\n"
			+ "            ELSE ei.denominazione\n"
			+ "    END as ente ,\n"
			+ "	CASE \n"
			+ "		WHEN ceu.des_categoria is not null THEN ceu.des_categoria\n"
			+ "            ELSE cei.des_categoria\n"
			+ "    END as categoriaEnte \n"
			+ "	\n"
			+ "	FROM \n"
			+ "		myext_evento_iscritti i \n"
			+ "		left join myext_utente u on u.id_utente = i.id_utente \n"
			+ "		left join myext_ente eu on u.id_ente = eu.id_ente \n"
			+ "		left join myext_ente ei on i.id_ente = ei.id_ente\n"
			+ "		left join myext_ente_categoria ceu on eu.id_categoria = ceu.id_categoria\n"
			+ "		left join myext_ente_categoria cei on ei.id_categoria = cei.id_categoria\n"
			+ "	\n"
			+ "	where \n"
			+ "	(i.flg_partecip_remoto=1 or i.flg_partecip_loco=1) and\n"
			+ "	i.id_evento in (:ids)\n"
			+ "	\n"
			+ "	order by evento asc, partecipante asc\n"
			+ "	;", nativeQuery = true)
	List<IStatPartecipantiRow> getStatPartecipantiRows(@Param("ids") List<String> ids);
	

	
}
