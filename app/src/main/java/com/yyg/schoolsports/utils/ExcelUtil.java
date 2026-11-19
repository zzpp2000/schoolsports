package com.yyg.schoolsports.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.yyg.schoolsports.Bean.BeanData;
import com.yyg.schoolsports.sqlite.MySQLHelp;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelUtil {
    List<Map<Integer, Object>> dataList = new ArrayList<>();
    Workbook workbook;
    String excelStr;
    InputStream inputStream;

    MySQLHelp mySQLHelp;
    ContentValues contentValues;
    Cursor cursor;
    private static WritableFont arial14font = null;
    private static WritableCellFormat arial14format = null;
    private static WritableFont arial10font = null;
    private static WritableCellFormat arial10format = null;
    private static WritableFont arial12font = null;
    private static WritableCellFormat arial12format = null;
    private final static String UTF8_ENCODING = "UTF-8";


    /**
     * 单元格的格式设置 字体大小 颜色 对齐方式、背景颜色等...
     */
    private static void format() {
        try {
            arial14font = new WritableFont(WritableFont.ARIAL, 14, WritableFont.BOLD);
            arial14font.setColour(jxl.format.Colour.LIGHT_BLUE);
            arial14format = new WritableCellFormat(arial14font);
            arial14format.setAlignment(jxl.format.Alignment.CENTRE);
            arial14format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            arial14format.setBackground(jxl.format.Colour.VERY_LIGHT_YELLOW);

            arial10font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
            arial10format = new WritableCellFormat(arial10font);
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);
            arial10format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            arial10format.setBackground(Colour.GRAY_25);

            arial12font = new WritableFont(WritableFont.ARIAL, 10);
            arial12format = new WritableCellFormat(arial12font);
            //对齐格式
            arial10format.setAlignment(jxl.format.Alignment.CENTRE);
            //设置边框
            arial12format.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);

        } catch (WriteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化Excel表格
     *
     * @param filePath  存放excel文件的路径（path/demo.xls）
     * @param sheetName Excel表格的表名
     * @param colName   excel中包含的列名（可以有多个）
     */
    public static void initExcel(String filePath, String sheetName, String[] colName) {
        format();
        WritableWorkbook workbook = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            } else {
                return;
            }
            workbook = jxl.Workbook.createWorkbook(file);
            //设置表格的名字
            WritableSheet sheet = workbook.createSheet(sheetName, 0);
            //创建标题栏
            sheet.addCell((WritableCell) new Label(0, 0, filePath, arial14format));
            for (int col = 0; col < colName.length; col++) {
                sheet.addCell(new Label(col, 0, colName[col], arial10format));
            }
            //设置行高
            sheet.setRowView(0, 340);
            workbook.write();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 将制定类型的List写入Excel中
     *
     * @param objList  待写入的list
     * @param fileName
     * @param c
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public static <T> void writeObjListToExcel(List<T> objList, String fileName, Context c) {
        if (objList != null && objList.size() > 0) {
            WritableWorkbook writebook = null;
            InputStream in = null;
            try {
                WorkbookSettings setEncode = new WorkbookSettings();
                setEncode.setEncoding(UTF8_ENCODING);

                in = new FileInputStream(new File(fileName));
                jxl.Workbook workbook = jxl.Workbook.getWorkbook(in);
                writebook = jxl.Workbook.createWorkbook(new File(fileName), workbook);
                WritableSheet sheet = writebook.getSheet(0);

                for (int j = 0; j < objList.size(); j++) {

                    BeanData demoBean = (BeanData) objList.get(j);
                    List<String> list = new ArrayList<>();
                    list.add(demoBean.getDate().toString());
                    list.add(demoBean.getStudentID());
                    list.add(demoBean.getStudentName());
                    list.add(demoBean.getGradeNum());
                    list.add(demoBean.getClassNum());
                    list.add(demoBean.getCardNum());
                    list.add(demoBean.getAge());
                    list.add(demoBean.getSex());
                    list.add(demoBean.getEthnic());
                    list.add(demoBean.getPhoto());
                    list.add(demoBean.getID());
                    list.add(demoBean.getBirthDate());
                    list.add(demoBean.getPhone());
                    list.add(demoBean.getStudentSource());
                    list.add(demoBean.getStudentAddress());
                    list.add(demoBean.getTestProject());
                    list.add(demoBean.getTestData());
                    list.add(demoBean.getStatus());

                    for (int i = 0; i < list.size(); i++) {
                        sheet.addCell(new Label(i, j + 1, list.get(i), arial12format));
                        if (list.get(i).length() <= 4) {
                            //设置列宽
                            sheet.setColumnView(i, list.get(i).length() + 8);
                        } else {
                            //设置列宽
                            sheet.setColumnView(i, list.get(i).length() + 5);
                        }
                    }
                    //设置行高
                    sheet.setRowView(j + 1, 350);
                }

                writebook.write();
                workbook.close();
                Toast.makeText(c, "导出Excel成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (writebook != null) {
                    try {
                        writebook.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    public static List<BeanData> readExcelToAssets(Context context,String fileName) {
        List<BeanData> contentBeans = new ArrayList<>();
        try {
            InputStream is = new FileInputStream(fileName);
            Workbook book = Workbook.getWorkbook(is);
            book.getNumberOfSheets();
            Sheet sheet = book.getSheet(0);
            int Rows = sheet.getRows();
            System.out.println(Rows+"");
            for (int i = 1; i < Rows; ++i) {
                String date = (sheet.getCell(0, i)).getContents();
                String studentID = (sheet.getCell(1, i)).getContents();
                String studentName = (sheet.getCell(2, i)).getContents();
                String gradeNum = (sheet.getCell(3, i)).getContents();
                String classNum = (sheet.getCell(4, i)).getContents();
                String cardNum = (sheet.getCell(5, i)).getContents();
                String age = (sheet.getCell(6, i)).getContents();
                String sex = (sheet.getCell(7, i)).getContents();
                String ethnic = (sheet.getCell(8, i)).getContents();
                String photo = (sheet.getCell(9, i)).getContents();
                String ID = (sheet.getCell(10, i)).getContents();
                String birthDate = (sheet.getCell(11, i)).getContents();
                String phone = (sheet.getCell(12, i)).getContents();
                String studentSource = (sheet.getCell(13, i)).getContents();
                String studentAddress = (sheet.getCell(14, i)).getContents();
                String TestProject = (sheet.getCell(15, i)).getContents();
                String TestData  = (sheet.getCell(16, i)).getContents();
                String Status  = (sheet.getCell(17, i)).getContents();
// 修改后的写法（加上 null 作为第一个参数）：
                contentBeans.add(new BeanData(null, date,studentID,studentName,gradeNum,classNum,cardNum,age,sex,ethnic,photo,ID,birthDate,phone,studentSource,studentAddress,TestProject,TestData,Status));
                Log.e("FileUtil", "第" + i + "行数据=\n" + date + "," + studentID + "," + studentName + "," + gradeNum + "," + classNum+ "," + cardNum+ "," + age+ "," + sex
                        + "," + ethnic+ "," + photo+ "," + ID+ "," + birthDate+ "," + phone+ "," + studentSource+ "," + studentAddress+ "," + TestProject+ "," + TestData+ "," + Status);

            }
            book.close();

        } catch (Exception e) {

            Log.e("FileUtil", "e" + e);
        }
        return contentBeans;
    }

    public static String getNowTime(){
        Date time=new Date();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyyMMdd");
        return sdf.format(time);
    }
}