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


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import it.regioneveneto.myp3.myextranet.bean.UtentiProdottiAttivatiEnteExcelReportInput;
import it.regioneveneto.myp3.myextranet.bean.UtentiProdottiAttivatiEnteExcelReportInput.ProdottoUtenti;
import it.regioneveneto.myp3.myextranet.bean.UtentiProdottiAttivatiEnteExcelReportInput.Utente;
import it.regioneveneto.myp3.myextranet.service.ExcelReportService;

@Service
public class ExcelReportServiceImpl implements ExcelReportService {
	private static final Logger LOG = LoggerFactory.getLogger(ExcelReportServiceImpl.class);
	
	private static final int MIN_ROW_PER_PRODOTTO = 6;

	public ExcelReportServiceImpl() {
		
	}

	@Override
	public byte[] generateUtentiProdottiAttivatiEnteExcelReport(UtentiProdottiAttivatiEnteExcelReportInput input) {
		Context ctx = new Context();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date now = new Date();
		
		String mainHeaderText = String.format("%s (%s)", input.getEnte(), sdf.format(now));
		
		Workbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet(input.getEnte());
		// column widths
		sheet.setColumnWidth(0, 8000);
		sheet.setColumnWidth(1, 6000);
		sheet.setColumnWidth(2, 4000);
		sheet.setColumnWidth(3, 4000);
		sheet.setColumnWidth(4, 4000);
		sheet.setColumnWidth(5, 4000);
		sheet.setColumnWidth(6, 4000);
		sheet.setColumnWidth(7, 4000);

		
		// main header
		Row mainHeader = sheet.createRow(0);
		mainHeader.setHeight((short) (mainHeader.getHeight() * 2));
		CellStyle mainHeaderStyle = workbook.createCellStyle();
		mainHeaderStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
		mainHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		mainHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
		mainHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		setBorder(mainHeaderStyle, BorderStyle.THIN); 
		
		HSSFFont mainHeaderFont = ((HSSFWorkbook) workbook).createFont();
		mainHeaderFont.setFontName("Arial");
		mainHeaderFont.setFontHeightInPoints((short) 10);
		mainHeaderFont.setBold(true);
		mainHeaderStyle.setFont(mainHeaderFont);
		
		Cell headerCell = mainHeader.createCell(0);
		headerCell.setCellValue(mainHeaderText);
		headerCell.setCellStyle(mainHeaderStyle);
		
		// merge cells 
		sheet.addMergedRegion(CellRangeAddress.valueOf("A1:H1"));
		
		
		// representative table
		Row reprTableHeader = sheet.createRow(1);
		reprTableHeader.setHeight((short) Math.round(reprTableHeader.getHeight() * 1.5));
		CellStyle reprTableHeaderHeaderStyle = workbook.createCellStyle();
		reprTableHeaderHeaderStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		reprTableHeaderHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		reprTableHeaderHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
		reprTableHeaderHeaderStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		setBorder(reprTableHeaderHeaderStyle, BorderStyle.THIN); 
		
		HSSFFont tableHeaderFont = ((HSSFWorkbook) workbook).createFont();
		tableHeaderFont.setFontName("Arial");
		tableHeaderFont.setFontHeightInPoints((short) 8);
		tableHeaderFont.setBold(true);
		reprTableHeaderHeaderStyle.setFont(tableHeaderFont);
		
		String[] values = { 
				"Ruolo",
				"Nome",
				"Cognome",
				"Telefono",
				"Email",
				"Codice Fiscale"
				//"Stato"
				};
		
		ctx.currentCellColumnIndex = 1;
		printValues(ctx, reprTableHeader, reprTableHeaderHeaderStyle, values);
		
		Row reprRow = sheet.createRow(2);
		CellStyle tableContentStyle = workbook.createCellStyle();
		tableContentStyle.setWrapText(true);
		setBorder(tableContentStyle, BorderStyle.THIN); 
		HSSFFont tableContentFont = ((HSSFWorkbook) workbook).createFont();
		tableContentFont.setFontName("Arial");
		tableContentFont.setFontHeightInPoints((short) 7);
		tableContentStyle.setFont(tableContentFont);
		ctx.currentCellColumnIndex = 1;
		printUtente(ctx, reprRow, tableContentStyle, input.getRappresentante());
		ctx.currentCellRowIndex = 2;

		// second table
		ctx.currentCellRowIndex += 3;
		Row prodTableHeader = sheet.createRow(ctx.currentCellRowIndex);
		prodTableHeader.setHeight((short) Math.round(prodTableHeader.getHeight() * 1.5));
		
		String[] values2 = { 
				"Prodotto",
				"Ruolo",
				"Nome",
				"Cognome",
				"Telefono",
				"Email",
				"Codice Fiscale",
				"Stato"
				};
		
		ctx.currentCellColumnIndex = 0;
		printValues(ctx, prodTableHeader, reprTableHeaderHeaderStyle, values2);

		// prodotti
		CellStyle prodottoNomeStyle = workbook.createCellStyle();
		prodottoNomeStyle.setWrapText(true);
		HSSFFont prodottoNomeFont = ((HSSFWorkbook) workbook).createFont();
		prodottoNomeFont.setFontName("Arial");
		prodottoNomeFont.setFontHeightInPoints((short) 12);
		prodottoNomeFont.setBold(true);
		prodottoNomeStyle.setFont(prodottoNomeFont);
		prodottoNomeStyle.setAlignment(HorizontalAlignment.CENTER);
		prodottoNomeStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		setBorder(prodottoNomeStyle, BorderStyle.THIN);

		
		List<ProdottoUtenti> prodotti = input.getProdotti();
		for (ProdottoUtenti prodotto : prodotti) {
			ctx.currentCellRowIndex++;
			printProdotto(ctx, sheet, prodottoNomeStyle, tableContentStyle, prodotto);
		}
		
		
		
		// write document

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			workbook.write(baos);
			workbook.close();
			
			return baos.toByteArray();
			
		} catch (IOException e) {
			LOG.error("Error writing workbook", e);
			try {
				workbook.close();
			} catch (IOException ioe) {
				LOG.error("Error closing workbook", ioe);
			}
		}
		
		return null;
	}

	private void printProdotto(Context ctx, Sheet sheet, CellStyle prodottoNomeStyle, CellStyle tableContentStyle,
			ProdottoUtenti prodotto) {
		int startRowIndex = ctx.currentCellRowIndex;
		
		List<Utente> utenti = prodotto.getUtenti();
		
		for (Utente utente : utenti) {
			ctx.currentCellColumnIndex = 1;
			Row row = sheet.createRow(ctx.currentCellRowIndex);
			printUtente(ctx, row, tableContentStyle, utente);
			
			ctx.currentCellRowIndex++;
		}
		
		Cell cell = sheet.getRow(startRowIndex).createCell(0);
		cell.setCellValue(prodotto.getDescrizioneProdotto());
		cell.setCellStyle(prodottoNomeStyle);
		
		// minimun number of rows per prodotto
		ctx.currentCellRowIndex = Math.max(ctx.currentCellRowIndex, startRowIndex + MIN_ROW_PER_PRODOTTO) - 1;
		int endRowIndex = ctx.currentCellRowIndex;
		
		// merge
		sheet.addMergedRegion(new CellRangeAddress(startRowIndex, endRowIndex, 0, 0));
	}

	private void printUtente(Context ctx, Row row, CellStyle style, Utente utente) {
		
		if (utente == null) return;
		
		String[] values = { 
				utente.getRuolo(),
				utente.getNome(),
				utente.getCognome(),
				utente.getTelefono(),
				utente.getEmail(),
				utente.getCodFiscale(),
				statoDecode(utente.getStato())
				};
		
		printValues(ctx, row, style, values);
	
	}
	
	private String statoDecode( String stato) {
		if(stato == null) return "Confermato";
		else if(stato.equals("INS")) return "Da inserire";
		else if(stato.equals("MOD")) return "Da modificare";
		else if(stato.equals("DEL")) return "Da rimuovere";		
		else return "Confermato";
	}
	
	private void printValues(Context ctx, Row row, CellStyle style, String[] values) {
		for (String v : values) {			
			Cell cell = row.createCell(ctx.currentCellColumnIndex++);
			cell.setCellValue(v);
			cell.setCellStyle(style);
		}
	}
	
	private void setBorder(CellStyle style, BorderStyle borderStyle) {
		style.setBorderBottom(borderStyle);
		style.setBorderTop(borderStyle);
		style.setBorderRight(borderStyle);
		style.setBorderLeft(borderStyle);
	}

	class Context {
		protected int currentAreaRowStart;
		protected int currentAreaRowEnd;
		protected int currentAreaColumnStart;
		protected int currentAreaColumnEnd;
		protected int currentCellRowIndex;
		protected int currentCellColumnIndex;
	}
}
