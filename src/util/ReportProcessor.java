package util;

import model.Group;
import model.Month;
import model.State;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.charts.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCatAx;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTitle;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTValAx;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReportProcessor {
    public static void makeReport(int groupIndex, XMLProcessor base) {
        Workbook workbook = new XSSFWorkbook();
        try {
            Group group = base.getGroups().get(groupIndex);
            FileOutputStream fos = new FileOutputStream(group.getGroupName() + "-" +
                    Calendar.getInstance().get(Calendar.YEAR) + ".xlsx");
            List<String> columnNames = new ArrayList<>(group.getColumnNames());
            for (int i = 0; i < columnNames.size(); i++) {
                Sheet sheet = workbook.createSheet(columnNames.get(i));
                double summary = 0D;
                for (int j = 0; j < group.getMonthInfo().size(); j++) {
                    Row row = sheet.createRow(j);
                    Cell cell1 = row.createCell(0);
                    Cell cell = row.createCell(1);
                    cell1.setCellValue(group.getMonthInfo().get(j).getMonth());
                    cell.setCellValue(group.getMonthInfo().get(j).getColumnSummary(i));
                    summary += group.getMonthInfo().get(j).getColumnSummary(i);
                }

                CellStyle cellStyle = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                cellStyle.setFont(font);
                Row row = sheet.createRow(13);
                Cell cell1 = row.createCell(0);
                cell1.setCellValue("Итог: ");
                cell1.setCellStyle(cellStyle);
                Cell cell2 = row.createCell(1);
                cell2.setCellValue(summary);
                cell2.setCellStyle(cellStyle);

                Drawing drawing = sheet.createDrawingPatriarch();
                ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 3, 2, 18, 15);

                Chart chart = drawing.createChart(anchor);
                ChartLegend legend = chart.getOrCreateLegend();
                legend.setPosition(LegendPosition.TOP_RIGHT);

                LineChartData data = chart.getChartDataFactory().createLineChartData();

                ChartAxis bottomAxis = chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
                ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
                leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

                ChartDataSource<Number> xs = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(0, 11, 0, 0));
                ChartDataSource<Number> ys = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(0, 11, 1, 1));

                LineChartSeries lineChartSeries = data.addSeries(xs, ys);
                lineChartSeries.setTitle(columnNames.get(i));

                setCatAxisTitle((XSSFChart) chart, 0, "Месяц");
                setValueAxisTitle((XSSFChart) chart, 0, "Сумма");

                chart.plot(data, bottomAxis, leftAxis);

                ArrayList<String> list = new ArrayList<>();
                list.add(columnNames.get(i));
                JFreeChart barChart = createYearChart(group, list);
                int width = 640;
                int height = 480;
                ByteArrayOutputStream chart_out = new ByteArrayOutputStream();
                ChartUtilities.writeChartAsPNG(chart_out,barChart,width,height);
                int my_picture_id = workbook.addPicture(chart_out.toByteArray(), Workbook.PICTURE_TYPE_PNG);
                chart_out.close();
                XSSFDrawing draw = (XSSFDrawing) sheet.createDrawingPatriarch();
                ClientAnchor my_anchor = new XSSFClientAnchor();
                my_anchor.setCol1(3);
                my_anchor.setRow1(18);
                XSSFPicture my_picture = draw.createPicture(my_anchor, my_picture_id);
                my_picture.resize();
            }

            Sheet sheet = workbook.createSheet("Итог");
            for(int j = 0; j < group.getMonthInfo().size(); j ++) {
                Row row = sheet.createRow(j);
                Cell cell = row.createCell(0);
                cell.setCellValue(group.getMonthInfo().get(j).getMonth());
                for(int i = 0; i < columnNames.size(); i++) {
                    cell = row.createCell(i + 1);
                    cell.setCellValue(group.getMonthInfo().get(j).getColumnSummary(i));
                }
            }

            Drawing drawing = sheet.createDrawingPatriarch();
            ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 5, 2, 20, 15);

            Chart chart = drawing.createChart(anchor);
            ChartLegend legend = chart.getOrCreateLegend();
            legend.setPosition(LegendPosition.TOP_RIGHT);


            LineChartData data = chart.getChartDataFactory().createLineChartData();

            ChartAxis bottomAxis = chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
            ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
            leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

            ChartDataSource<Number> xs = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(0,
                    11, 0, 0));

            for (int i = 0; i < group.getColumnNames().size(); i++) {
                ChartDataSource<Number> ys = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(0,
                        11, i + 1, i + 1));
                LineChartSeries lineChartSeries = data.addSeries(xs, ys);
                lineChartSeries.setTitle(columnNames.get(i));
            }

            setCatAxisTitle((XSSFChart) chart, 0, "Месяц");
            setValueAxisTitle((XSSFChart) chart, 0, "Сумма");

            chart.plot(data, bottomAxis, leftAxis);

            JFreeChart barChart = createYearChart(group, columnNames);
            int width=850;
            int height=480;
            ByteArrayOutputStream chart_out = new ByteArrayOutputStream();
            ChartUtilities.writeChartAsPNG(chart_out, barChart, width, height);
            int my_picture_id = workbook.addPicture(chart_out.toByteArray(), Workbook.PICTURE_TYPE_PNG);
            chart_out.close();
            XSSFDrawing draw = (XSSFDrawing) sheet.createDrawingPatriarch();
            ClientAnchor my_anchor = new XSSFClientAnchor();
            my_anchor.setCol1(5);
            my_anchor.setRow1(18);
            XSSFPicture my_picture = draw.createPicture(my_anchor, my_picture_id);
            my_picture.resize();

            workbook.write(fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void makeReport(int groupIndex, int monthIndex, XMLProcessor base) {
        Workbook workbook = new XSSFWorkbook();
        try {
            Group group = base.getGroups().get(groupIndex);
            Month month = group.getMonthInfo().get(monthIndex);
            FileOutputStream fos = new FileOutputStream(group.getGroupName() + "-" + month.getMonth() + "-" +
                    Calendar.getInstance().get(Calendar.YEAR) + ".xlsx");

            for (State state : month.getStateList()) {
                Sheet sheet = workbook.createSheet(state.getName());
                for (int i = 0; i < state.getDailyData().size(); i++) {
                    Row row = sheet.createRow(i);
                    Cell cell1 = row.createCell(0);
                    Cell cell = row.createCell(1);
                    cell1.setCellValue(i + 1);
                    cell.setCellValue(Double.parseDouble(state.getDailyData().get(i).toString()));
                }
                CellStyle cellStyle = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);
                cellStyle.setFont(font);
                Row row = sheet.createRow(State.DAYS_IN_MONTH + 1);
                Cell cell1 = row.createCell(0);
                cell1.setCellValue("Итог: ");
                cell1.setCellStyle(cellStyle);
                Cell cell2 = row.createCell(1);
                cell2.setCellValue(state.getSummary());
                cell2.setCellStyle(cellStyle);

                Drawing drawing = sheet.createDrawingPatriarch();
                ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 3, 2, 18, 15);

                Chart chart = drawing.createChart(anchor);
                ChartLegend legend = chart.getOrCreateLegend();
                legend.setPosition(LegendPosition.TOP_RIGHT);

                LineChartData data = chart.getChartDataFactory().createLineChartData();

                ChartAxis bottomAxis = chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
                ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
                leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

                ChartDataSource<Number> xs = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(0, State.DAYS_IN_MONTH - 1, 0, 0));
                ChartDataSource<Number> ys = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(0, State.DAYS_IN_MONTH - 1, 1, 1));

                LineChartSeries lineChartSeries = data.addSeries(xs, ys);
                lineChartSeries.setTitle(state.getName());

                setCatAxisTitle((XSSFChart) chart, 0, "День");
                setValueAxisTitle((XSSFChart) chart, 0, "Сумма");

                chart.plot(data, bottomAxis, leftAxis);

                ArrayList<State> list = new ArrayList<State>();
                list.add(state);
                JFreeChart barChart = createMonthChart(list);
                int width=640;
                int height=480;
                ByteArrayOutputStream chart_out = new ByteArrayOutputStream();
                ChartUtilities.writeChartAsPNG(chart_out,barChart,width,height);
                int my_picture_id = workbook.addPicture(chart_out.toByteArray(), Workbook.PICTURE_TYPE_PNG);
                chart_out.close();
                XSSFDrawing draw = (XSSFDrawing) sheet.createDrawingPatriarch();
                ClientAnchor my_anchor = new XSSFClientAnchor();
                my_anchor.setCol1(3);
                my_anchor.setRow1(18);
                XSSFPicture my_picture = draw.createPicture(my_anchor, my_picture_id);
                my_picture.resize();
            }

            Sheet sheet = workbook.createSheet("Итог");
            for (int k = 0; k < State.DAYS_IN_MONTH; k++) {
                Row row = sheet.createRow(k);
                Cell cell = row.createCell(0);
                cell.setCellValue(k + 1);
                for (int i = 0; i < month.getStateList().size(); i++) {
                    cell = row.createCell(i + 1);
                    cell.setCellValue(Double.valueOf(month.getStateList().get(i).getDailyData().get(k).toString()));
                }
            }
            CellStyle cellStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            cellStyle.setFont(font);
            Row row = sheet.createRow(State.DAYS_IN_MONTH + 1);
            Cell cell = row.createCell(0);
            cell.setCellValue("Итог: ");
            cell.setCellStyle(cellStyle);
            for (int i = 0; i < month.getStateList().size(); i++) {
                cell = row.createCell(i + 1);
                cell.setCellValue(month.getStateList().get(i).getSummary());
                cell.setCellStyle(cellStyle);
            }

            Drawing drawing = sheet.createDrawingPatriarch();
            ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 5, 2, 20, 15);

            Chart chart = drawing.createChart(anchor);
            ChartLegend legend = chart.getOrCreateLegend();
            legend.setPosition(LegendPosition.TOP_RIGHT);


            LineChartData data = chart.getChartDataFactory().createLineChartData();

            ChartAxis bottomAxis = chart.getChartAxisFactory().createCategoryAxis(AxisPosition.BOTTOM);
            ValueAxis leftAxis = chart.getChartAxisFactory().createValueAxis(AxisPosition.LEFT);
            leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);

            ChartDataSource<Number> xs = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(0,
                    State.DAYS_IN_MONTH - 1, 0, 0));

            for (int i = 0; i < month.getStateList().size(); i++) {
                ChartDataSource<Number> ys = DataSources.fromNumericCellRange(sheet, new CellRangeAddress(0,
                        State.DAYS_IN_MONTH - 1, i + 1, i + 1));
                LineChartSeries lineChartSeries = data.addSeries(xs, ys);
                lineChartSeries.setTitle(month.getStateList().get(i).getName());
            }

            setCatAxisTitle((XSSFChart) chart, 0, "День");
            setValueAxisTitle((XSSFChart) chart, 0, "Сумма");

            chart.plot(data, bottomAxis, leftAxis);

            JFreeChart barChart = createMonthChart((ArrayList<State>) month.getStateList());
            int width=850;
            int height=480;
            ByteArrayOutputStream chart_out = new ByteArrayOutputStream();
            ChartUtilities.writeChartAsPNG(chart_out, barChart, width, height);
            int my_picture_id = workbook.addPicture(chart_out.toByteArray(), Workbook.PICTURE_TYPE_PNG);
            chart_out.close();
            XSSFDrawing draw = (XSSFDrawing) sheet.createDrawingPatriarch();
            ClientAnchor my_anchor = new XSSFClientAnchor();
            my_anchor.setCol1(5);
            my_anchor.setRow1(18);
            XSSFPicture my_picture = draw.createPicture(my_anchor, my_picture_id);
            my_picture.resize();

            workbook.write(fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setCatAxisTitle(XSSFChart chart, int axisIdx, String title) {
        CTCatAx valAx = chart.getCTChart().getPlotArea().getCatAxArray(axisIdx);
        CTTitle ctTitle = valAx.addNewTitle();
        ctTitle.addNewLayout();
        ctTitle.addNewOverlay().setVal(false);
        CTTextBody rich = ctTitle.addNewTx().addNewRich();
        rich.addNewBodyPr();
        rich.addNewLstStyle();
        CTTextParagraph p = rich.addNewP();
        p.addNewPPr().addNewDefRPr();
        p.addNewR().setT(title);
        p.addNewEndParaRPr();
    }

    private static void setValueAxisTitle(XSSFChart chart, int axisIdx, String title) {
        CTValAx valAx = chart.getCTChart().getPlotArea().getValAxArray(axisIdx);
        CTTitle ctTitle = valAx.addNewTitle();
        ctTitle.addNewLayout();
        ctTitle.addNewOverlay().setVal(false);
        CTTextBody rich = ctTitle.addNewTx().addNewRich();
        rich.addNewBodyPr();
        rich.addNewLstStyle();
        CTTextParagraph p = rich.addNewP();
        p.addNewPPr().addNewDefRPr();
        p.addNewR().setT(title);
        p.addNewEndParaRPr();
    }

    public static JFreeChart createMonthChart(ArrayList<State> stateList) {
        DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
        for (State state : stateList) {
            java.util.List list = state.getDailyData();
            for(int i = 0; i < list.size(); i++) {
                dataSet.setValue((Number) Double.parseDouble(list.get(i).toString()), state.getName(), i + 1);
            }
        }
        JFreeChart chart = ChartFactory.createBarChart("", "День", "Сумма",
                dataSet, PlotOrientation.VERTICAL, false, true, false);
        CategoryPlot p = chart.getCategoryPlot();
        p.setRangeGridlinePaint(java.awt.Color.black);

        return chart;
    }

    public static JFreeChart createYearChart(Group group, List<String> columnNames) {

        DefaultCategoryDataset yearData = new DefaultCategoryDataset();
        for(int i = 0; i < columnNames.size(); i++) {
            for (int j = 0; j < group.getMonthInfo().size(); j++) {
                yearData.setValue((Number) (group.getMonthInfo().get(j).getColumnSummary(i)),
                        i, j + 1);
            }

        }
        return ChartFactory.createBarChart("", "Месяц", "Сумма", yearData,
                PlotOrientation.VERTICAL, false, true, false);
    }
}

