package ru.study.odin.odinbot.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import ru.study.odin.odinbot.tdlib.ChatMember;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class ExcelService {

    public String createExcelFile(Collection<ChatMember> chatMembers, String filename) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Участники группы");
        int columnWidth = 8000;
        sheet.setColumnWidth(0, columnWidth);
        sheet.setColumnWidth(1, columnWidth);
        sheet.setColumnWidth(2, columnWidth);
        sheet.setColumnWidth(3, columnWidth);
        sheet.setColumnWidth(4, columnWidth);
        Row header = sheet.createRow(0);
        
        CellStyle headerStyle = getHeaderStyle(workbook);
        setHeaderCells(header, headerStyle);

        AtomicInteger i = new AtomicInteger(1);
        chatMembers.forEach(
                chatMember -> {
                    Row row = sheet.createRow(i.getAndIncrement());
                    Cell cell = row.createCell(0);
                    cell.setCellValue(chatMember.getFirstName());
                    cell = row.createCell(1);
                    cell.setCellValue(chatMember.getLastName());
                    cell = row.createCell(2);
                    cell.setCellValue(chatMember.getPhoneNumber());
                    cell = row.createCell(3);
                    String username = chatMember.getUsername() == null ? "" : "@" + chatMember.getUsername();
                    cell.setCellValue(username);
                    cell = row.createCell(4);
                    cell.setCellValue(chatMember.getStatus());
                }
        );

        return createFile(filename, workbook);
    }

    public boolean deleteFile(String filepath) {
        File file = new File(filepath);
        return file.delete();
    }

    private static String createFile(String filename, Workbook workbook) {
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "temp/" + filename + ".xlsx";
        try {
            FileOutputStream outputStream = new FileOutputStream(fileLocation);
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileLocation;
    }

    private static void setHeaderCells(Row header, CellStyle headerStyle) {
        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("Имя");
        headerCell.setCellStyle(headerStyle);
        headerCell = header.createCell(1);
        headerCell.setCellValue("Фамилия");
        headerCell.setCellStyle(headerStyle);
        headerCell = header.createCell(2);
        headerCell.setCellValue("Телефон");
        headerCell.setCellStyle(headerStyle);
        headerCell = header.createCell(3);
        headerCell.setCellValue("Никнейм");
        headerCell.setCellStyle(headerStyle);
        headerCell = header.createCell(4);
        headerCell.setCellValue("Статус");
        headerCell.setCellStyle(headerStyle);
    }

    @NotNull
    private static CellStyle getHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        headerStyle.setFont(font);
        return headerStyle;
    }

}
