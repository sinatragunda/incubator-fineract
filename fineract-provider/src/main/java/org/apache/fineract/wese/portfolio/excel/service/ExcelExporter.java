/*

    Created by Sinatra Gunda
    At 1:06 AM on 9/5/2021

*/
package org.apache.fineract.wese.portfolio.excel.service;

import java.io.File;
import java.util.Map ;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class ExcelExporter {

    public static File export(List<IExcelExportable> excelExportableList ,IExcelExportable excelExportable ,File file){

        // get first item then get header ,this time export all
        Map<Integer ,Object[]> rowMap = new HashMap<>();

        Consumer<IExcelExportable> toTemplateObject = (e)->{
            Object[] objects = e.excelTemplate();
            int count = rowMap.size();
            rowMap.put(++count ,objects);
            //++count ;
        };

        Object[] headers = excelExportable.excelHeader();

        excelExportableList.stream().forEach(toTemplateObject);
        ExcelWriter excelWriter = new ExcelWriter(0 ,true);

        Consumer<Object> writeFile = (e)->{
            excelWriter.write(file ,null ,rowMap ,headers ,"");
        };

        Optional.ofNullable(rowMap).ifPresent(writeFile);
        return file ;

    }


}
