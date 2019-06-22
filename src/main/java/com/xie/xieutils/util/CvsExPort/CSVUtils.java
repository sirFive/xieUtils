package com.xie.xieutils.util.CvsExPort;


import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @description: CSV操作类
 * @author: xiewucheng
 * @create: 2019-06-22 09:26
 **/
public class CSVUtils<T> {
    /**
     * 数据类型
     */
    private Class<T> cls;
    /**
     * 需要导出的数据
     */
    private List<T> list;

    /**
     * 输出的文件名
     */
    private String fileName;

    /**
     * 文件导出路径
     */
    private String outPutPath;

    /**
     * 方法和标题：标题[0][] ，方法名[1][]
     */
    private final String[][] titlesAndMethods = new String[2][];

    public CSVUtils(List<T> list, Class<T> cls, String fileName, String outPutPath) {
        this.list = list;
        this.cls = cls;
        this.fileName = fileName;
        this.outPutPath = outPutPath;
        initTitleAndMethod(cls);
    }

    /**
     * 导出
     * @return
     * @throws NoSuchMethodException
     * @throws IOException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public File doExport() throws NoSuchMethodException, IOException, IllegalAccessException, InvocationTargetException {
        File csvFile ;
        File file = new File(outPutPath);
        if (!file.exists()) {
            file.mkdir();
        }
        //定义文件名格式并创建
        csvFile = File.createTempFile(fileName, ".csv", new File(outPutPath));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                csvFile), "GBK"), 1024);
        writeBody(writer,list);
        writer.flush();
        writer.close();
        return csvFile;
    }

    /**
     * 写数据
     * @param writer
     * @param list
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws IOException
     */
    public void writeBody(BufferedWriter writer, List<T> list) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, IOException{
        String[] titles = titlesAndMethods[0];
        for (int i = 0; i < titles.length; i++) {
            writer.write(titles[i] != null ? new String(
                    (titles[i]).getBytes("GBK"), "GBK") : "");
            writer.write(",");
        }
        writer.newLine();

        //数据遍历
        for(T obj: list){
            int i = 0;
            int length = titlesAndMethods[1].length;
            for (int j = 0; j < titlesAndMethods[0].length && titlesAndMethods[1][j] != null; j++) {
                Object result = this.cls.getMethod(this.titlesAndMethods[1][j]).invoke(obj,new Object[] {});
                String str = null;
                if(result == null)
                    //处理空值
                    str = "";
                else if(result instanceof Date){
                    //处理日期
                   str = new SimpleDateFormat("yyyy-MM-dd").format((Date)result);
                } else
                    //处理文本中的"
                    str = result.toString().replaceAll("\"","\"\"");

                if(i++ <=length-1)
                    //文本用双引号包裹
                    writer.write("\""+str+"\",");
                else
                    //最后的元素需要使用换行符而不是“，” 需要特别注意
                    writer.write("\""+str+"\"");
            }
            //换行
            writer.newLine();
        }

    }

    /**
     * 初始化标题和方法
     * @param clazz
     */
    private void initTitleAndMethod(Class<?> clazz) {
        ExportAnnotation exportAnnotation ;
        Field[] fields = clazz.getDeclaredFields();
        {
            int length = fields.length;
            this.titlesAndMethods[0] = new String[length + 1];
            this.titlesAndMethods[1] = new String[length + 1];
        }
        int index;
        for (Field field : fields) {
            exportAnnotation = field.getAnnotation(ExportAnnotation.class);
            if (exportAnnotation != null) {
                index = exportAnnotation.order();
                //标题
                this.titlesAndMethods[0][index] = exportAnnotation.columnTitle();
                //方法
                this.titlesAndMethods[1][index] = exportAnnotation.method();
            }
        }
    }
}
