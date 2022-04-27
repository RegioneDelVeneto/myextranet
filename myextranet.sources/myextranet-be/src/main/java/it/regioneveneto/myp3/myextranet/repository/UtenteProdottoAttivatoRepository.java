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
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.regioneveneto.myp3.myextranet.bean.IStatNomeValoreRow;
import it.regioneveneto.myp3.myextranet.model.ProdottoAttivato;
import it.regioneveneto.myp3.myextranet.model.RuoloProdotto;
import it.regioneveneto.myp3.myextranet.model.Utente;
import it.regioneveneto.myp3.myextranet.model.UtenteProdottoAttivato;

public interface UtenteProdottoAttivatoRepository extends JpaRepository<UtenteProdottoAttivato, Integer>, JpaSpecificationExecutor<UtenteProdottoAttivato> {

	List<UtenteProdottoAttivato> findAllByProdottoAttivatoOrderByRuoloCodRuoloAscIdUtenteProdAsc(ProdottoAttivato prodotto);
	
	List<UtenteProdottoAttivato> findAllByProdottoAttivatoAndFlgEnabledOrderByRuoloCodRuoloAscIdUtenteProdAsc(ProdottoAttivato prodotto, Integer flgEnabled);
	
	@Query(value ="select \n" + 
			"utenteprod0_.id_utente_prod, \n" +
            "utenteprod0_.dt_ins, \n" +
            "utenteprod0_.dt_ult_mod, \n" +
            "utenteprod0_.id_oper_ins, \n" +
            "utenteprod0_.id_oper_ult_mod, \n" +
            "utenteprod0_.oper_ins, \n" +
            "utenteprod0_.oper_ult_mod, \n" +
            "utenteprod0_.cod_fiscale, \n" +
            "utenteprod0_.cognome, \n" +
            "utenteprod0_.email, \n" +
            "utenteprod0_.flg_enabled, \n" +
            "utenteprod0_.nome, \n" +
            "utenteprod0_.id_attivazione, \n" +
            "utenteprod0_.cod_ruolo, \n" +
            "utenteprod0_.telefono, \n" +
            "utenteprod0_.id_utente, \n" +
            "utente2_.cognome \n" +
			"        from\n" + 
			"            myext_prodotto_attiv_utenti utenteprod0_ \n" + 
			"        left outer join\n" + 
			"            myext_prodotto_ruoli ruoloprodo1_ \n" + 
			"                on utenteprod0_.cod_ruolo=ruoloprodo1_.cod_ruolo \n" + 
			"        left outer join\n" + 
			"            myext_utente utente2_ \n" + 
			"                on utenteprod0_.id_utente=utente2_.id_utente \n" + 
			"        where\n" + 
			"            utenteprod0_.id_attivazione =:id_attivazione\n" + 
			"            and utenteprod0_.flg_enabled= :flg_enabled \n" + 
			"        order by\n" + 
			"            upper(coalesce(utente2_.cognome, utenteprod0_.cognome)) asc\n" + 
			" ", nativeQuery = true)
	List<UtenteProdottoAttivato> findAllByProdottoAttivatoAndFlgEnabledOrderByRuoloCodRuoloAscUtenteCognomeAscQuery(@Param("id_attivazione") Integer id_attivazione,@Param("flg_enabled") Integer flg_enabled);

	@Query("SELECT u FROM UtenteProdottoAttivato u WHERE u.prodottoAttivato = :prodottoAttivato and u.ruolo = :ruolo and u.codFiscale = :codFiscale")
	List<UtenteProdottoAttivato> findRecordsByProdottoAttivatoAndCodFiscaleAndRuoloProdotto(
			@Param("prodottoAttivato") ProdottoAttivato prodottoAttivato, @Param("codFiscale") String codFiscale, @Param("ruolo") RuoloProdotto ruolo);
	
	@Query("SELECT u FROM UtenteProdottoAttivato u WHERE u.prodottoAttivato = :prodottoAttivato and u.ruolo = :ruolo and u.codFiscale = :codFiscale and u.flgEnabled = :flgEnabled")
	List<UtenteProdottoAttivato> findRecordsByProdottoAttivatoAndCodFiscaleAndRuoloProdottoAndFlgEnabled(
			@Param("prodottoAttivato") ProdottoAttivato prodottoAttivato, @Param("codFiscale") String codFiscale, @Param("ruolo") RuoloProdotto ruolo, @Param("flgEnabled") Integer flgEnabled);

	List<UtenteProdottoAttivato> findAllByProdottoAttivatoIdAttivazioneAndFlgEnabled(Integer idAttivazione, Integer flgEnabled);
	
	@Query(value = "select\n" + 
			"            utenteprod0_.id_utente_prod,\n" + 
			"            utenteprod0_.dt_ins,\n" + 
			"            utenteprod0_.dt_ult_mod,\n" + 
			"            utenteprod0_.id_oper_ins,\n" + 
			"            utenteprod0_.id_oper_ult_mod,\n" + 
			"            utenteprod0_.oper_ins,\n" + 
			"            utenteprod0_.oper_ult_mod,\n" + 
			"            utenteprod0_.cod_fiscale,\n" + 
			"            utenteprod0_.cognome,\n" + 
			"            utenteprod0_.email,\n" + 
			"            utenteprod0_.flg_enabled,\n" + 
			"            utenteprod0_.nome,\n" + 
			"            utenteprod0_.id_attivazione,\n" + 
			"            utenteprod0_.cod_ruolo,\n" + 
			"            utenteprod0_.telefono,\n" + 
			"            utenteprod0_.id_utente,\n" + 
			"			utente2_.cognome\n" + 
			"        from\n" + 
			"            myext_prodotto_attiv_utenti utenteprod0_ \n" + 
			"        left outer join\n" + 
			"            myext_prodotto_attivato prodottoat1_ \n" + 
			"                on utenteprod0_.id_attivazione=prodottoat1_.id_attivazione \n" + 
			"        left outer join\n" + 
			"            myext_utente utente2_ \n" + 
			"                on utenteprod0_.id_utente=utente2_.id_utente \n" + 
			"        where\n" + 
			"            prodottoat1_.id_attivazione=:idAttivazione\n" + 
			"            and utenteprod0_.flg_enabled=:flgEnabled\n" + 
			"        order by\n" + 
			"		upper(coalesce(utente2_.cognome, utenteprod0_.cognome)) asc\n" + 
			"", nativeQuery = true)
	List<UtenteProdottoAttivato> findAllByProdottoAttivatoIdAttivazioneAndFlgEnabledOrderByUtenteCognomeAscQuery(@Param("idAttivazione") Integer idAttivazione, @Param("flgEnabled") Integer flgEnabled);
    
	@Query(value = "SELECT \n"
			+ "	'OperatoriProdotti'\\:\\:varchar as nome,\n"
			+ "	count(distinct u.cod_fiscale) as valore\n"
			+ "FROM \n"
			+ "	myext_prodotto_attiv_utenti u\n"
			+ "	join myext_prodotto_attivato p on u.id_attivazione = p.id_attivazione\n"
			+ "WHERE\n"
			+ "	u.flg_enabled = 1\n"
			+ "	and p.dt_inizio_val <= now() and p.dt_fine_val > now()\n"
			+ "", nativeQuery = true)
	IStatNomeValoreRow getNumOperatoriProdotti();

}
