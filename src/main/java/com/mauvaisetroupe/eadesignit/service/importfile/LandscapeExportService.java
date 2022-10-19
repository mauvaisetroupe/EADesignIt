package com.mauvaisetroupe.eadesignit.service.importfile;

import com.mauvaisetroupe.eadesignit.domain.Application;
import com.mauvaisetroupe.eadesignit.domain.DataFlow;
import com.mauvaisetroupe.eadesignit.domain.FlowInterface;
import com.mauvaisetroupe.eadesignit.domain.FunctionalFlow;
import com.mauvaisetroupe.eadesignit.domain.FunctionalFlowStep;
import com.mauvaisetroupe.eadesignit.domain.LandscapeView;
import com.mauvaisetroupe.eadesignit.domain.enumeration.ProtocolType;
import com.mauvaisetroupe.eadesignit.repository.ApplicationRepository;
import com.mauvaisetroupe.eadesignit.repository.LandscapeViewRepository;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FontFormatting;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LandscapeExportService {

    @Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    LandscapeViewRepository landscapeViewRepository;

    public ByteArrayOutputStream getLandscapeExcel(Long landscapeId) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet appliSheet = workbook.createSheet("Application");
        Sheet flowSheet = workbook.createSheet("Message_Flow");

        writeFlows(flowSheet, landscapeId);
        autoSizeAllColumns(flowSheet);
        writeApplication(appliSheet);
        autoSizeAllColumns(appliSheet);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        workbook.write(stream);
        workbook.close();
        return stream;
    }

    private void autoSizeAllColumns(Sheet sheet) {
        int nbColumns = sheet.getRow(0).getPhysicalNumberOfCells();
        for (int i = 0; i < nbColumns; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void writeFlows(Sheet sheet, Long landscapeId) {
        LandscapeView landscapeView = landscapeViewRepository.getById(landscapeId);
        int column = 0;
        int rownb = 0;
        Row headerRow = sheet.createRow(rownb++);
        headerRow.createCell(column++).setCellValue(FlowImportService.FLOW_ID_FLOW);
        headerRow.createCell(column++).setCellValue(FlowImportService.FLOW_ALIAS_FLOW);
        headerRow.createCell(column++).setCellValue(FlowImportService.FLOW_SOURCE_ELEMENT);
        headerRow.createCell(column++).setCellValue(FlowImportService.FLOW_TARGET_ELEMENT);
        headerRow.createCell(column++).setCellValue(FlowImportService.FLOW_DESCRIPTION);
        Cell stepNumberHeader = headerRow.createCell(column++);
        stepNumberHeader.setCellValue(FlowImportService.FLOW_STEP_NUMBER);
        addComment(sheet, stepNumberHeader, "Not used during import process, only display helper");
        headerRow.createCell(column++).setCellValue(FlowImportService.FLOW_STEP_DESCRIPTION);
        headerRow.createCell(column++).setCellValue(FlowImportService.FLOW_INTEGRATION_PATTERN);
        headerRow.createCell(column++).setCellValue(FlowImportService.FLOW_FREQUENCY);
        headerRow.createCell(column++).setCellValue(FlowImportService.FLOW_FORMAT);
        headerRow.createCell(column++).setCellValue(FlowImportService.FLOW_SWAGGER);
        headerRow.createCell(column++).setCellValue(FlowImportService.FLOW_BLUEPRINT_SOURCE);
        headerRow.createCell(column++).setCellValue(FlowImportService.FLOW_BLUEPRINT_TARGET);
        headerRow.createCell(column++).setCellValue(FlowImportService.FLOW_COMMENT);

        for (FunctionalFlow flow : landscapeView.getFlows()) {
            for (FunctionalFlowStep step : flow.getSteps()) {
                FlowInterface interface1 = step.getFlowInterface();
                Row row = sheet.createRow(rownb++);
                column = 0;
                row.createCell(column++).setCellValue(interface1.getAlias());
                row.createCell(column++).setCellValue(flow.getAlias());
                row.createCell(column++).setCellValue(interface1.getSource().getName());
                row.createCell(column++).setCellValue(interface1.getTarget().getName());
                row.createCell(column++).setCellValue(flow.getDescription());
                row.createCell(column++).setCellValue(step.getStepOrder());
                row.createCell(column++).setCellValue(step.getDescription());
                row.createCell(column++).setCellValue(interface1.getProtocol() != null ? interface1.getProtocol().getName() : "");
                if (interface1.getDataFlows() != null && interface1.getDataFlows().size() == 1) {
                    DataFlow dataFlow = interface1.getDataFlows().iterator().next();
                    row.createCell(column++).setCellValue(dataFlow.getFrequency() != null ? dataFlow.getFrequency().toString() : "");
                    row.createCell(column++).setCellValue(dataFlow.getFormat() != null ? dataFlow.getFormat().getName() : "");
                    if (interface1.getProtocol() != null && interface1.getProtocol().getType().equals(ProtocolType.API)) {
                        row.createCell(column++).setCellValue(dataFlow.getContractURL());
                    } else {
                        row.createCell(column++).setCellValue("N/A");
                    }
                } else {
                    row.createCell(column++).setCellValue("multiple");
                    row.createCell(column++).setCellValue("multiple");
                    row.createCell(column++).setCellValue("multiple");
                }
                row.createCell(column++).setCellValue(flow.getDocumentationURL());
                row.createCell(column++).setCellValue(flow.getDocumentationURL2());
                row.createCell(column++).setCellValue(flow.getComment());
            }
        }
        // Add conditional formatting if Application doesn't exist
        SheetConditionalFormatting sheetCF = sheet.getSheetConditionalFormatting();
        ConditionalFormattingRule rule = sheetCF.createConditionalFormattingRule("COUNTIF(Application!$B$2:$B$1000,c2)<=0");
        FontFormatting fontFmt = rule.createFontFormatting();
        fontFmt.setFontStyle(false, true);
        fontFmt.setFontColorIndex(IndexedColors.RED.index);
        ConditionalFormattingRule[] cfRules = new ConditionalFormattingRule[] { rule };
        CellRangeAddress[] regions = new CellRangeAddress[] { CellRangeAddress.valueOf("C2:D200") };
        sheetCF.addConditionalFormatting(regions, cfRules);
    }

    public void addComment(Sheet sheet, Cell cell, String commentText) {
        CreationHelper factory = sheet.getWorkbook().getCreationHelper();
        //get an existing cell or create it otherwise:

        ClientAnchor anchor = factory.createClientAnchor();
        //i found it useful to show the comment box at the bottom right corner
        // anchor.setCol1(cell.getColumnIndex() + 1); //the box of the comment starts at this given column...
        // anchor.setCol2(cell.getColumnIndex() + 3); //...and ends at that given column
        // anchor.setRow1(cell.getRowIndex() + 1); //one row below the cell...
        // anchor.setRow2(cell.getRowIndex() + 5); //...and 4 rows high

        Drawing drawing = sheet.createDrawingPatriarch();
        Comment comment = drawing.createCellComment(anchor);
        //set the comment text and author
        comment.setString(factory.createRichTextString(commentText));
        cell.setCellComment(comment);
    }

    private void writeApplication(Sheet sheet) {
        List<Application> applications = applicationRepository.findAll();
        int column = 0;
        int rownb = 0;
        Row headerRow = sheet.createRow(rownb++);
        headerRow.createCell(column++).setCellValue("application.id");
        headerRow.createCell(column++).setCellValue("application.name");

        for (Application application : applications) {
            column = 0;
            Row row = sheet.createRow(rownb++);
            row.createCell(column++).setCellValue(application.getAlias());
            row.createCell(column++).setCellValue(application.getName());
        }
    }
}