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
package it.regioneveneto.myp3.myextranet.service.impl;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.regioneveneto.myp3.myextranet.exception.MyExtranetException;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetValidationException;
import it.regioneveneto.myp3.myextranet.model.AuditModel;
import it.regioneveneto.myp3.myextranet.model.ProcedimentoProdotto;
import it.regioneveneto.myp3.myextranet.model.RichiestaProdotto;
import it.regioneveneto.myp3.myextranet.model.StepProcedimentoProdotto;
import it.regioneveneto.myp3.myextranet.repository.ProcedimentoProdottoRepository;
import it.regioneveneto.myp3.myextranet.repository.RichiestaProdottoRepository;
import it.regioneveneto.myp3.myextranet.repository.StepProcedimentoProdottoRepository;
import it.regioneveneto.myp3.myextranet.service.ProcedimentoProdottoService;
import it.regioneveneto.myp3.myextranet.utils.AuditUtils;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;
import it.regioneveneto.myp3.myextranet.utils.RepositoryUtils;
import it.regioneveneto.myp3.myextranet.web.dto.OperazioneStepProcedimentoProdottoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.ProcedimentoProdottoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProcedimentoProdottoFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProcedimentoProdottoUpdateStepsDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProcedimentoProdottoWithStepsDTO;
import it.regioneveneto.myp3.myextranet.web.dto.StepProcedimentoProdottoDTO;

@Service
public class ProcedimentoProdottoServiceImpl implements ProcedimentoProdottoService {
    private static final Logger LOG = LoggerFactory.getLogger(ProcedimentoProdottoServiceImpl.class);

    @Autowired
    ProcedimentoProdottoRepository procedimentoProdottoRepository;   
    
    @Autowired
    StepProcedimentoProdottoRepository stepProcedimentoProdottoRepository;
    
    @Autowired
    RichiestaProdottoRepository richiestaProdottoRepository;
    
	public ProcedimentoProdottoServiceImpl() {
		super();
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public PagedData<ProcedimentoProdottoDTO> getProcedimentiProdotto(ProcedimentoProdottoFilterDTO procedimentoProdottoFilter, int pageNumber,
			int pageSize, String sortProperty, String sortDirection) {
		if (sortProperty == null) sortProperty = "idProdottoProc";
		if (sortDirection == null) sortDirection = "ASC";
		
		Pageable pageable = PagedData.buildPageable(pageNumber, pageSize, sortProperty, sortDirection);
		
		Page<ProcedimentoProdotto> page = procedimentoProdottoRepository.findAll(RepositoryUtils.buildProcedimentoProdottoFilterSpecification(procedimentoProdottoFilter), pageable);

		PagedData<ProcedimentoProdottoDTO> pagedData = new PagedData<ProcedimentoProdottoDTO>(page, ProcedimentoProdottoDTO.class);
		return pagedData;

	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public ProcedimentoProdottoWithStepsDTO getProcedimentoProdottoPrimoStep(Integer idProdottoAtt, String codTipoRich) throws Exception {
		
		Optional<ProcedimentoProdotto> procedimentoProdottoOptional = procedimentoProdottoRepository.findOneByProdottoIdProdottoAttAndTipoRichiestaProdottoCodTipoRichAndFlgEnabled(idProdottoAtt, codTipoRich, 1);
		if (procedimentoProdottoOptional.isPresent()) {
			ProcedimentoProdotto procedimentoProdotto = procedimentoProdottoOptional.get();
			ProcedimentoProdottoWithStepsDTO procedimentoProdottoWithSteps = ObjectMapperUtils.map(procedimentoProdotto, ProcedimentoProdottoWithStepsDTO.class);
			
			// get first step
			Optional<StepProcedimentoProdotto> stepOptional = stepProcedimentoProdottoRepository.findOneByProcedimentoProdottoAndNumStep(procedimentoProdotto, 1);
			
			if (stepOptional.isPresent()) {
				procedimentoProdottoWithSteps.getStep().add(ObjectMapperUtils.map(stepOptional.get(), StepProcedimentoProdottoDTO.class));
			}
			
			return procedimentoProdottoWithSteps;
		}
		
		return null;
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public ProcedimentoProdottoWithStepsDTO getProcedimentoProdotto(Integer idProdottoProc) throws Exception {
								
		Optional<ProcedimentoProdotto> procedimentoProdottoOptional = procedimentoProdottoRepository.findById(idProdottoProc);

		if(procedimentoProdottoOptional.isPresent()) {
			ProcedimentoProdottoWithStepsDTO procedimentoProdotto = ObjectMapperUtils.map(procedimentoProdottoOptional.get(), ProcedimentoProdottoWithStepsDTO.class);
			
			populateSteps(procedimentoProdotto);
			
			return procedimentoProdotto;
		} else {
			throw new Exception("ProcedimentoProdotto not found");
		}

	}

	private void populateSteps(ProcedimentoProdottoWithStepsDTO procedimentoProdotto) {
		List<StepProcedimentoProdotto> steps = stepProcedimentoProdottoRepository.findAllByProcedimentoProdottoIdProdottoProcOrderByNumStep(procedimentoProdotto.getIdProdottoProc());		
		procedimentoProdotto.setStep(ObjectMapperUtils.mapAll(steps, StepProcedimentoProdottoDTO.class));
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public void aggiornaStepProcedimentoProdotto(@Valid ProcedimentoProdottoUpdateStepsDTO procedimentoProdotto)
			throws Exception {
		
		Integer idProdottoProc = procedimentoProdotto.getIdProdottoProc();
		
		boolean isNewProcedimento = (idProdottoProc == null);
		
		if (isNewProcedimento) {
			// insert new ProcedimentoProdotto
			ProcedimentoProdottoDTO insertedProcedimento = insertProcedimentoProdotto(procedimentoProdotto);
			idProdottoProc = insertedProcedimento.getIdProdottoProc();
			
		} else {
			// update ProcedimentoProdotto
			updateProcedimentoProdotto(procedimentoProdotto, true);
		}
		
		List<OperazioneStepProcedimentoProdottoDTO> operazioniStep = procedimentoProdotto.getOperazioniStep();
		
		for (OperazioneStepProcedimentoProdottoDTO operazione : operazioniStep) {
			switch (operazione.getTipoOperazione()) {
			case Constants.STEP_PROCEDIMENTO_PRODOTTO_OPERAZIONE_ELIMINA:
				eliminaStepProcedimentoProdotto(operazione.getStepProcedimentoProdotto());
				break;
			case Constants.STEP_PROCEDIMENTO_PRODOTTO_OPERAZIONE_MODIFICA:
				modificaStepProcedimentoProdotto(operazione.getStepProcedimentoProdotto(), idProdottoProc);
				break;

			default:
				break;
			}
		}
	}
	
	private void modificaStepProcedimentoProdotto(StepProcedimentoProdottoDTO stepProcedimentoProdotto, Integer idProdottoProc) throws Exception {
				
		if (stepProcedimentoProdotto == null) {
			throw new MyExtranetException("Impossibile modificare un record null");
		}		
		
		// set the correct reference
		Optional<ProcedimentoProdotto> procedimentoProdottoOptional = procedimentoProdottoRepository.findById(idProdottoProc);
		if (procedimentoProdottoOptional.isPresent()) {
			stepProcedimentoProdotto.setProcedimentoProdotto(ObjectMapperUtils.map(procedimentoProdottoOptional.get(), ProcedimentoProdottoDTO.class));
		} else {
			throw new MyExtranetException("Riferimento al Procedimento Prodotto non corretto");
		}
		
		if (stepProcedimentoProdotto.getIdStatoConf() == null) {
			// insert
			
			insertStepProcedimentoProdotto(stepProcedimentoProdotto);
		} else {
			// update				
			updateStepProcedimentoProdotto(stepProcedimentoProdotto, false);
		}
	}

	private StepProcedimentoProdottoDTO updateStepProcedimentoProdotto(StepProcedimentoProdottoDTO patchStepProcedimentoProdotto, boolean isPatch) throws Exception {
		
		// retrieve original
		Optional<StepProcedimentoProdotto> originalStepProcedimentoProdottoOptional = stepProcedimentoProdottoRepository.findById(patchStepProcedimentoProdotto.getIdStatoConf());
		if (!originalStepProcedimentoProdottoOptional.isPresent()) {
			throw new Exception("Step procedimento prodotto not found");
		}
		StepProcedimentoProdotto originalStepProcedimentoProdotto = originalStepProcedimentoProdottoOptional.get();
		
		// prevent modification of used codStato
		String codStato = originalStepProcedimentoProdotto.getCodStato();
		Integer idProdottoProc = originalStepProcedimentoProdotto.getProcedimentoProdotto().getIdProdottoProc();
		List<RichiestaProdotto> richieste = richiestaProdottoRepository.findAllByProcedimentoProdottoIdProdottoProcAndCodStatoAndFlgFineRich(idProdottoProc, codStato, 0);
		
		if (richieste.size() > 0) {
			// there are requests using that codStato, don't change it
			patchStepProcedimentoProdotto.setCodStato(codStato);
		}


		StepProcedimentoProdotto stepProcedimentoProdotto = StepProcedimentoProdottoDTO.patchStepProcedimentoProdotto(patchStepProcedimentoProdotto, originalStepProcedimentoProdotto, isPatch, true);
		
		// set audit fields
		AuditModel audit = (AuditModel) stepProcedimentoProdotto;
		AuditUtils.fillAudit(audit);
		
		// checks
		checkForConflicts(stepProcedimentoProdotto);
				
		StepProcedimentoProdotto savedStepProcedimentoProdotto = stepProcedimentoProdottoRepository.save(stepProcedimentoProdotto);
		
		return ObjectMapperUtils.map(savedStepProcedimentoProdotto, StepProcedimentoProdottoDTO.class);
	}

	private StepProcedimentoProdottoDTO insertStepProcedimentoProdotto(@Valid StepProcedimentoProdottoDTO newStepProcedimentoProdotto) throws Exception {
		
		StepProcedimentoProdotto stepProcedimentoProdotto = ObjectMapperUtils.map(newStepProcedimentoProdotto, StepProcedimentoProdotto.class);
		
		// some checks
		if (stepProcedimentoProdotto.getProcedimentoProdotto() == null || stepProcedimentoProdotto.getCodStato() == null) {
			throw new Exception("ProcedimentoProdotto e CodStato sono obbligatori");
		}
		
		// add audit fields
		AuditModel audit = (AuditModel) stepProcedimentoProdotto;
		AuditUtils.fillAudit(audit);
				
		// checks
		checkForConflicts(stepProcedimentoProdotto);
		
		StepProcedimentoProdotto savedStepProcedimentoProdotto = stepProcedimentoProdottoRepository.save(stepProcedimentoProdotto);
		
		return ObjectMapperUtils.map(savedStepProcedimentoProdotto, StepProcedimentoProdottoDTO.class);
		
	}

	private void eliminaStepProcedimentoProdotto(StepProcedimentoProdottoDTO stepProcedimentoProdotto) throws MyExtranetException {
		
		if (stepProcedimentoProdotto == null || stepProcedimentoProdotto.getIdStatoConf() == null) {
			throw new MyExtranetException("Impossibile eliminare un record senza id");
		}
		
		// check whether it's legal to remove the step (check for references)
		Optional<StepProcedimentoProdotto> stepOptional = stepProcedimentoProdottoRepository.findById(stepProcedimentoProdotto.getIdStatoConf());
		if (!stepOptional.isPresent()) {
			throw new MyExtranetException("Record da eliminare inesistente");
		}
		
		String codStato = stepOptional.get().getCodStato();
		Integer idProdottoProc = stepOptional.get().getProcedimentoProdotto().getIdProdottoProc();
		List<RichiestaProdotto> richieste = richiestaProdottoRepository.findAllByProcedimentoProdottoIdProdottoProcAndCodStatoAndFlgFineRich(idProdottoProc, codStato, 0);
		
		if (richieste.size() > 0) {
			throw new MyExtranetException("Impossibile eliminare uno step attualmente in uso in una richiesta");
		}
		
		stepProcedimentoProdottoRepository.deleteById(stepProcedimentoProdotto.getIdStatoConf());
	}

	public ProcedimentoProdottoDTO insertProcedimentoProdotto(@Valid ProcedimentoProdottoDTO newProcedimentoProdotto) throws Exception {
		
		ProcedimentoProdotto procedimento = ObjectMapperUtils.map(newProcedimentoProdotto, ProcedimentoProdotto.class);
		
		// add audit fields
		AuditModel audit = (AuditModel) procedimento;
		AuditUtils.fillAudit(audit);
		
		fillDefaults(procedimento);

		// checks
		checkForConflicts(procedimento);
		
		ProcedimentoProdotto savedProcedimento = procedimentoProdottoRepository.save(procedimento);
				
		return ObjectMapperUtils.map(savedProcedimento, ProcedimentoProdottoDTO.class);
	}

	private void fillDefaults(ProcedimentoProdotto procedimento) {
		
		if (procedimento.getNumVersione() == null) {
			procedimento.setNumVersione(1);
		}
	}

	private void checkForConflicts(ProcedimentoProdotto procedimento) throws MyExtranetException {
		int count = 0;
		
		Integer idProdottoProc = procedimento.getIdProdottoProc();
		if (idProdottoProc == null) {
			count = procedimentoProdottoRepository.countConflictingRecords(procedimento.getProdotto().getIdProdottoAtt(), procedimento.getTipoRichiestaProdotto().getCodTipoRich());			
		} else {
			count = procedimentoProdottoRepository.countConflictingRecords(procedimento.getProdotto().getIdProdottoAtt(), procedimento.getTipoRichiestaProdotto().getCodTipoRich(), idProdottoProc);	
		}
		
		if (count > 0) {
			throw new MyExtranetValidationException("Esiste già un procedimento per lo stesso Prodotto e Tipo Richiesta");
		}
	}
	
	private void checkForConflicts(StepProcedimentoProdotto stepProcedimentoProdotto) throws MyExtranetException {
		int count = 0;
		
		Integer idStatoConf = stepProcedimentoProdotto.getIdStatoConf();
		if (idStatoConf == null) {
			count = stepProcedimentoProdottoRepository.countConflictingRecords(stepProcedimentoProdotto.getCodStato(), stepProcedimentoProdotto.getProcedimentoProdotto().getIdProdottoProc());			
		} else {
			count = stepProcedimentoProdottoRepository.countConflictingRecords(stepProcedimentoProdotto.getCodStato(), stepProcedimentoProdotto.getProcedimentoProdotto().getIdProdottoProc(), idStatoConf);	
		}
		
		if (count > 0) {
			throw new MyExtranetException("There is already a record with same CodStato and Procedimento Prodotto");
		}
		
	}

	public ProcedimentoProdottoDTO updateProcedimentoProdotto(ProcedimentoProdottoDTO patchProcedimentoProdotto, boolean isPatch) throws Exception {
		
		// retrieve original
		Optional<ProcedimentoProdotto> originalProcedimentoProdottoOptional = procedimentoProdottoRepository.findById(patchProcedimentoProdotto.getIdProdottoProc());
		if (!originalProcedimentoProdottoOptional.isPresent()) {
			throw new Exception("Procedimento Prodotto not found");
		}
						
		ProcedimentoProdotto originalProcedimentoProdotto = originalProcedimentoProdottoOptional.get();
		
		ProcedimentoProdotto procedimento = ProcedimentoProdottoDTO.patchProcedimentoProdotto(patchProcedimentoProdotto, originalProcedimentoProdotto, isPatch, true);
		
		// set audit fields
		AuditModel audit = (AuditModel) procedimento;
		AuditUtils.fillAudit(audit);
		
		// checks
		checkForConflicts(procedimento);

		ProcedimentoProdotto savedProcedimentoProdotto = procedimentoProdottoRepository.save(procedimento);
		
		return ObjectMapperUtils.map(savedProcedimentoProdotto, ProcedimentoProdottoDTO.class);

	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public ProcedimentoProdottoDTO deleteProcedimentoProdotto(Integer idProdottoProc) throws Exception {
		// retrieve original
		Optional<ProcedimentoProdotto> originalProcedimentoProdottoOptional = procedimentoProdottoRepository.findById(idProdottoProc);
		if (!originalProcedimentoProdottoOptional.isPresent()) {
			throw new Exception("Procedimento Prodotto not found");
		}
		ProcedimentoProdotto originalProcedimentoProdotto = originalProcedimentoProdottoOptional.get();
		ProcedimentoProdottoDTO procedimentoProdotto = ObjectMapperUtils.map(originalProcedimentoProdotto, ProcedimentoProdottoDTO.class);
		
		// delete any related steps first
		stepProcedimentoProdottoRepository.deleteAllByProcedimentoProdotto(originalProcedimentoProdotto);
		
		procedimentoProdottoRepository.delete(originalProcedimentoProdotto);
				
		return procedimentoProdotto;
	}

}
