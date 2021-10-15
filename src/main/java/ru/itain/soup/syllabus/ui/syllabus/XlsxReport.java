package ru.itain.soup.syllabus.ui.syllabus;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Setter
public class XlsxReport {
    final private List<SyllabusBlock> data;

    final private XSSFWorkbook workbook;

    XSSFCellStyle cellStyle;
    XSSFCellStyle headerStyle;
    XSSFCellStyle rotatedHeaderStyle;
    XSSFSheet sheet;

    public XlsxReport(List<SyllabusBlock> data) throws IOException {
        workbook = new XSSFWorkbook(XlsxReport.class.getClassLoader().getResourceAsStream("syllabus-report-template.xlsx"));
        cellStyle = workbook.createCellStyle();

        headerStyle = workbook.createCellStyle();
        rotatedHeaderStyle = workbook.createCellStyle();
        sheet = workbook.getSheetAt(0);
        this.data = data;

        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);


        font = workbook.createFont();
        font.setFontName("Arial");
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        rotatedHeaderStyle.setFont(font);
        rotatedHeaderStyle.setRotation((short) 90);
        fillReport(4, 0);
    }

    public void fillReport(int row, int startCol) {

        for (SyllabusBlock block : data) {

            List<Integer> blockRows = Lists.newArrayList();
            int col = startCol;
            XSSFRow r = sheet.createRow(row);
            XSSFCell cell = r.createCell(col);
            cell.setCellValue(block.getHead());
            cell.setCellStyle(headerStyle);
            sheet.addMergedRegion(new CellRangeAddress(row, row, col, 56));
            row++;
            for (SyllabusArticle article : block.getArticles()) {
                Integer startArticle = null;
                col = startCol;
                r = sheet.createRow(row);
                cell = r.createCell(col);
                cell.setCellValue(article.getHead());
                cell.setCellStyle(headerStyle);
                sheet.addMergedRegion(new CellRangeAddress(row, row, col, 56));
                row++;
                for (SyllabusRow syllabusRow : article.getRows()) {
                    col = startCol;
                    if (startArticle == null) {
                        startArticle = row;
                    }
                    r = sheet.createRow(row);
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(syllabusRow.getIndex());

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(syllabusRow.getDiscipline());


                    Optional<SyllabusRow> o = Optional.of(syllabusRow);

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getBFertileUnits).filter(v -> v > 0).ifPresent(cell::setCellValue);


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getVFertileUnits).filter(v -> v > 0).ifPresent(cell::setCellValue);

                    CellRangeAddress fertailRange = new CellRangeAddress(row, row, col - 1, col);

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);


                    cell.setCellFormula(String.format("SUM(%s)*36", fertailRange.formatAsString()));
                    CellRangeAddress totalHoursAddress = new CellRangeAddress(row, row, col, col);

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    CellRangeAddress hoursWithTeacherAddress = new CellRangeAddress(row, row, col, col);


                    CellRangeAddress weightAddress = new CellRangeAddress(row, row, col + 3, col + 3);
                    cell.setCellFormula(String.format("%1$s-(%1$s/3)-%2$s*18", totalHoursAddress.formatAsString(), weightAddress.formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    cell.setCellFormula(String.format("SUM(%s)", fertailRange.formatAsString()));


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    cell.setCellFormula(String.format("%s", totalHoursAddress.formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getUndefiningParameter).filter(v -> v > 0).ifPresent(cell::setCellValue);


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getLectures).filter(v -> v > 0).ifPresent(cell::setCellValue);


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getSeminars).filter(v -> v > 0).ifPresent(cell::setCellValue);

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getGroupExercises).filter(v -> v > 0).ifPresent(cell::setCellValue);


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getGroupLessons).filter(v -> v > 0).ifPresent(cell::setCellValue);

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getLaboratoryWorks).filter(v -> v > 0).ifPresent(cell::setCellValue);

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getPracticalLessons).filter(v -> v > 0).ifPresent(cell::setCellValue);

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getSpecialLessons).filter(v -> v > 0).ifPresent(cell::setCellValue);


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getCourseWorks).filter(v -> v > 0).ifPresent(cell::setCellValue);

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getConferences).filter(v -> v > 0).ifPresent(cell::setCellValue);

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getPractices).filter(v -> v > 0).ifPresent(cell::setCellValue);

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getTests).filter(v -> v > 0).ifPresent(cell::setCellValue);

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getCredit).filter(v -> v > 0).ifPresent(cell::setCellValue);

                    col++;

                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    CellRangeAddress examHoursAddress = new CellRangeAddress(row, row, col + 1, col + 1);

                    cell.setCellFormula(String.format("%1$s-%2$s-%3$s", totalHoursAddress.formatAsString(), hoursWithTeacherAddress.formatAsString(), examHoursAddress.formatAsString()));

                    col++;

                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    cell.setCellFormula(String.format("%1$s*24", weightAddress.formatAsString()));


                    ///=============1 курс===========================================

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY1s1i).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY1s1t).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY1s1s).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY1s2i).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY1s2t).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY1s2s).filter(v -> v > 0).ifPresent(cell::setCellValue);


                    ///=============2 курс===========================================

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY2s1i).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY2s1t).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY2s1s).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY2s2i).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY2s2t).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY2s2s).filter(v -> v > 0).ifPresent(cell::setCellValue);


                    ///=============3 курс===========================================

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY3s1i).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY3s1t).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY3s1s).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY3s2i).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY3s2t).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY3s2s).filter(v -> v > 0).ifPresent(cell::setCellValue);

                    ///=============4 курс===========================================

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY4s1i).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY4s1t).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY4s1s).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY4s2i).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY4s2t).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY4s2s).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    ///=============5 курс===========================================

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY5s1i).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY5s1t).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY5s1s).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY5s2i).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY5s2t).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(cellStyle);
                    o.map(SyllabusRow::getY5s2s).filter(v -> v > 0).ifPresent(cell::setCellValue);

// формы контроля

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    o.map(SyllabusRow::getExamControl).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    o.map(SyllabusRow::getGradedCreditControl).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    o.map(SyllabusRow::getPassWithoutAssessmentControl).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    o.map(SyllabusRow::getCourseWorkControl).filter(v -> v > 0).ifPresent(cell::setCellValue);
                    row++;
                }


                // итого за раздел
                if (startArticle != null) {
                    blockRows.add(row);
                    col = startCol;
                    r = sheet.createRow(row);
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellValue("Итого за раздел");
                    sheet.addMergedRegion(new CellRangeAddress(row, row, col, col + 1));

                    col += 2;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));
                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));


                    col += 2;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));


                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));

                    col++;
                    cell = r.createCell(col);
                    cell.setCellStyle(headerStyle);
                    cell.setCellFormula(String.format("SUM(%s)", new CellRangeAddress(startArticle, row - 1, col, col).formatAsString()));


                    row++;
                }

            }
            // итого за блок

            if (!block.getArticles().isEmpty()) {

                col = startCol;
                r = sheet.createRow(row);
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                cell.setCellValue("Итого за блок");
                sheet.addMergedRegion(new CellRangeAddress(row, row, col, col + 1));
                col += 2;
                cell = r.createCell(col);

                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }

                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col += 2;

                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }

                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }
                col++;
                cell = r.createCell(col);
                cell.setCellStyle(headerStyle);
                {
                    int finalCol = col;
                    cell.setCellFormula(String.format("SUM(%s)", blockRows.stream().map(y -> new CellRangeAddress(y, y, finalCol, finalCol)).map(CellRangeAddress::formatAsString).collect(Collectors.joining(","))));
                }

                row++;
            }
        }
    }


    public void write(File file) throws IOException {
        workbook.write(new FileOutputStream(file));
    }

    public void write(OutputStream stream) throws IOException {
        workbook.write(stream);
    }
}
