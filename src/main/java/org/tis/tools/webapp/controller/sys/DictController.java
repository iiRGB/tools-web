package org.tis.tools.webapp.controller.sys;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.tis.tools.base.exception.ToolsRuntimeException;
import org.tis.tools.common.utils.ExcelReader;
import org.tis.tools.model.def.JNLConstants;
import org.tis.tools.model.po.sys.SysDict;
import org.tis.tools.model.po.sys.SysDictItem;
import org.tis.tools.rservice.sys.capable.IDictRService;
import org.tis.tools.webapp.controller.BaseController;
import org.tis.tools.webapp.log.OperateLog;
import org.tis.tools.webapp.log.ReturnType;
import org.tis.tools.webapp.util.AjaxUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaoch on 2017/7/18.
 */
@Controller
@RequestMapping("/DictController")
public class DictController extends BaseController {

    private Map<String, Object> responseMsg ;

    @Autowired
    IDictRService dictRService;

    @ResponseBody
    @RequestMapping("importDict")
    public void importDict(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
        try {
            if (logger.isInfoEnabled()) {
                logger.info("importDict request : import dict data from" + file.getName());
            }
            String filename = file.getOriginalFilename();
            InputStream inputStream = file.getInputStream();
            ExcelReader excelReader = new ExcelReader(filename, inputStream);
           
            // 对读取Excel表格内容测试
            Map<Integer, Map<Integer, Object>> map = excelReader.readExcelContent("业务字典");

            // 遍历结果集,逐条插入
            for (int i = 1; i <= map.size(); i++) {
                Map rs = map.get(i);
                if (rs.get(0) != null && rs.get(0) != "") {
                    SysDict sysDict = new SysDict();
                    sysDict.setDictName(rs.get(1).toString());
                    sysDict.setDictKey(rs.get(0).toString());
                    sysDict.setDictType(rs.get(5).toString());
                    sysDict.setDictDesc(rs.get(2).toString());

                    SysDict retSysdict = dictRService.addDict(sysDict);
                    Map<Integer, Map<Integer, Object>> map2 = excelReader.readExcelContent2(sysDict.getDictKey());
                    for(int k = 3;k <= map2.size(); k++){
                        SysDictItem dictItem = new SysDictItem();
                        Map rs2 = map2.get(k);
                        if(rs2.get(0) != null && rs2.get(0) != "") {
                            dictItem.setItemName(rs2.get(2).toString());
                            dictItem.setItemValue(rs2.get(0).toString());
                            dictItem.setSendValue(rs2.get(1).toString());
                            dictItem.setGuidDict(retSysdict.getGuid());
                            dictRService.addDictItem(dictItem);
                        }
                    }
                }
            }
        } catch (ToolsRuntimeException e) {
            AjaxUtils.ajaxJsonErrorMessage(response,e.getCode(), e.getMessage());
            logger.error("appAdd exception : ", e);
        }catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response,"SYS_0001", e.getMessage());
            logger.error("appAdd exception : ", e);
        }

    }

    /**
     * 新增业务字典
     * @param content
     * @return
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,
            operateDesc = "新增业务字典",
            retType = ReturnType.Object,
            id = "guid",
            name = "dictName",
            keys = {"dictKey", "dictType"}
    )
    @ResponseBody
    @RequestMapping(value="/createSysDict", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> createSysDict(@RequestBody String content) {
        SysDict sysDict = JSONObject.parseObject(content, SysDict.class);
        return getReturnMap(dictRService.addDict(sysDict));
    }

    /**
     * 删除业务字典
     * @param content
     * @return
     * @throws ToolsRuntimeException
     * @throws ParseException
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,
            operateDesc = "删除业务字典",
            retType = ReturnType.Object,
            id = "guid",
            name = "dictName",
            keys = {"dictKey", "dictType"}
    )
    @ResponseBody
    @RequestMapping(value="/deleteSysDict", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> deleteSysDict(@RequestBody String content) {
        JSONObject jsonObject= JSONObject.parseObject(content);
        String dictGuid = jsonObject.getString("dictGuid");
        return getReturnMap(dictRService.deleteDict(dictGuid));
    }

    /**
     * 修改业务字典
     * @param content
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "修改业务字典",
            retType = ReturnType.Object,
            id = "guid",
            name = "dictName",
            keys = {"dictKey", "dictType"}
    )
    @ResponseBody
    @RequestMapping(value="/editSysDict", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> editSysDict(@RequestBody String content) {
        SysDict sysDict = JSONObject.parseObject(content, SysDict.class);
        dictRService.editSysDict(sysDict);
        return getReturnMap(sysDict);
    }

    /**
     * 查询单个业务字典
     * @param content
     */
    @ResponseBody
    @RequestMapping(value="/querySysDict", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> querySysDict(@RequestBody String content) {
        JSONObject jsonObject= JSONObject.parseObject(content);
        String dictGuid = jsonObject.getString("dictGuid");
        SysDict sysDict = dictRService.querySysDictByGuid(dictGuid);
        return getReturnMap(sysDict);
    }

    /**
     * 新增业务字典项
     * @param content
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_ADD,
            operateDesc = "新增业务字典项",
            retType = ReturnType.Object,
            id = "guid",
            name = "itemName",
            keys = {"guidDict", "itemType"}
    )
    @ResponseBody
    @RequestMapping(value="/createSysDictItem", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> createSysDictItem(@RequestBody String content) {
        SysDictItem sysDictItem = JSONObject.parseObject(content, SysDictItem.class);
        return getReturnMap(dictRService.addDictItem(sysDictItem));
    }

    /**
     * 删除业务字典项
     * @param content
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_DELETE,
            operateDesc = "删除业务字典项",
            retType = ReturnType.Object,
            id = "guid",
            name = "itemName",
            keys = {"guidDict", "itemType"}
    )
    @ResponseBody
    @RequestMapping(value="/deleteSysDictItem", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> deleteSysDictItem(@RequestBody String content) {
        JSONObject jsonObject= JSONObject.parseObject(content);
        String dictItemGuid = jsonObject.getString("dictItemGuid");
        return getReturnMap(dictRService.deleteDictItem(dictItemGuid));
    }

    /**
     * 修改业务字典项
     * @param content
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "修改业务字典项",
            retType = ReturnType.Object,
            id = "guid",
            name = "itemName",
            keys = {"guidDict", "itemType"}
    )
    @ResponseBody
    @RequestMapping(value="/editSysDictItem", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> editSysDictItem(@RequestBody String content) {
        SysDictItem sysDictItem = JSONObject.parseObject(content, SysDictItem.class);
        dictRService.editSysDictItem(sysDictItem);
        return getReturnMap(sysDictItem);
    }

    /**
     * 查询单个业务字典项
     * @param content
     */
    @ResponseBody
    @RequestMapping(value="/querySysDictItem", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> querySysDictItem(@RequestBody String content) {
        JSONObject jsonObject= JSONObject.parseObject(content);
        String dictItemGuid = jsonObject.getString("dictItemGuid");
        SysDictItem sysDict = dictRService.querySysDictItemByGuid(dictItemGuid );
       return getReturnMap(sysDict);
    }
    
    
    /**
     * 查询所有业务字典
     */
    @ResponseBody
    @RequestMapping(value="/querySysDictList" ,produces = "application/json;charset=UTF-8",method= RequestMethod.POST)
    public Map<String, Object> querySysDictList(@RequestBody String content){
        JSONObject jsonObject= JSONObject.parseObject(content);
        String isQueryRoot = jsonObject.getString("isQueryRoot");
        return getReturnMap(dictRService.querySysDicts(isQueryRoot)) ;
    }

    /**
     * 根据业务字典的guid查询对应所有字典项
     * @param content
     */
    @ResponseBody
    @RequestMapping(value="/querySysDictItemList" ,produces = "application/json;charset=UTF-8",method= RequestMethod.POST)
    public Map<String, Object> querySysDictItemList(@RequestBody String content) {
        JSONObject jsonObject= JSONObject.parseObject(content);
        String dictGuid = jsonObject.getString("dictGuid");
        return getReturnMap(dictRService.querySysDictItems(dictGuid));
    }
    
    
    /**
     * 根据key查询业务字典项列表
     *
     */
    @ResponseBody
    @RequestMapping(value="/queryDictItemListByDictKey", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryDictItemListByDictKey(@RequestBody String content) {
        JSONObject jsonObject= JSONObject.parseObject(content);
        String dictKey = jsonObject.getString("dictKey");
        return getReturnMap(dictRService.queryDictItemListByDictKey(dictKey));
    }
    
    /**
     * 查询所有字典项
     */
    @ResponseBody
    @RequestMapping(value="/queryAllDictItem", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryAllDictItem() {
        return getReturnMap(dictRService.querySysDictItemList());
    }


    /**
     * 导出所有业务字典为excel
     *
     */
    @ResponseBody
    @RequestMapping(value="/exportDictExcel",method= RequestMethod.GET)
    public void exportDictExcel(HttpServletRequest request, HttpServletResponse response) {
        OutputStream out = null;
        try {
            if (logger.isInfoEnabled()) {
                logger.info("exportDictExcel request ");
            }
            // 生成提示信息，
            response.setContentType("application/vnd.ms-excel");
            // 进行转码，使其支持中文文件名
            String codedFileName = URLEncoder.encode("业务字典数据", "UTF-8");
            response.setHeader("content-disposition", "attachment;filename=" + codedFileName + ".xlsx");

            List<SysDict> sysDicts = dictRService.querySysDicts(null);
            List<SysDictItem> sysDictItems = dictRService.querySysDictItemList();

            XSSFWorkbook wb = new XSSFWorkbook();  //--->创建了一个excel文件
            XSSFSheet sheet = wb.createSheet("业务字典");   //--->创建了一个工作簿

            sheet.setColumnWidth((short)0, 20* 256);    //---》设置单元格宽度，因为一个单元格宽度定了那么下面多有的单元格高度都确定了所以这个方法是sheet的
            sheet.setColumnWidth((short)1, 30* 256);    //---》设置单元格宽度，因为一个单元格宽度定了那么下面多有的单元格高度都确定了所以这个方法是sheet的
            sheet.setColumnWidth((short)2, 10* 256);    //---》设置单元格宽度，因为一个单元格宽度定了那么下面多有的单元格高度都确定了所以这个方法是sheet的
            sheet.setColumnWidth((short)3, 20* 256);    //---》设置单元格宽度，因为一个单元格宽度定了那么下面多有的单元格高度都确定了所以这个方法是sheet的
            sheet.setColumnWidth((short)4, 10* 256);    //--->第一个参数是指哪个单元格，第二个参数是单元格的宽度
            sheet.setColumnWidth((short)5, 50* 256);    //--->第一个参数是指哪个单元格，第二个参数是单元格的宽度
            sheet.setColumnWidth((short)6, 20* 256);    //--->第一个参数是指哪个单元格，第二个参数是单元格的宽度
            sheet.setColumnWidth((short)7, 20* 256);    //--->第一个参数是指哪个单元格，第二个参数是单元格的宽度
            sheet.setColumnWidth((short)8, 20* 256);    //--->第一个参数是指哪个单元格，第二个参数是单元格的宽度
            sheet.setColumnWidth((short)9, 20* 256);    //--->第一个参数是指哪个单元格，第二个参数是单元格的宽度
            sheet.setColumnWidth((short)10, 20* 256);    //--->第一个参数是指哪个单元格，第二个参数是单元格的宽度
            sheet.setColumnWidth((short)11, 20* 256);    //--->第一个参数是指哪个单元格，第二个参数是单元格的宽度
            sheet.setColumnWidth((short)12, 20* 256);    //--->第一个参数是指哪个单元格，第二个参数是单元格的宽度
            sheet.setDefaultRowHeight((short)300);    // ---->设置统一单元格的高度，就用这个方法

            //表格第一行
            XSSFRow row1 = sheet.createRow(0);   //--->创建一行
            String[] columnList = {
                    SysDict.COLUMN_GUID, SysDict.COLUMN_DICT_KEY, SysDict.COLUMN_DICT_TYPE, SysDict.COLUMN_DICT_NAME,
                    SysDict.COLUMN_FROM_TYPE, SysDict.COLUMN_DICT_DESC, SysDict.COLUMN_GUID_PARENTS, SysDict.COLUMN_DEFAULT_VALUE,
                    SysDict.COLUMN_FROM_TABLE, SysDict.COLUMN_USE_FOR_KEY, SysDict.COLUMN_USE_FOR_NAME, SysDict.COLUMN_SQL_FILTER,
                    SysDict.COLUMN_SEQNO };
            for (short i = 0; i < columnList.length; i++) {
                row1.createCell(i).setCellValue(columnList[i]);//--->创建单元格
            }
            int j = 1;
            for (SysDict dict : sysDicts) {
                XSSFRow row = sheet.createRow(j);   //--->创建一行
                row.createCell(0).setCellValue(dict.getGuid());
                row.createCell(1).setCellValue(dict.getDictKey());
                row.createCell(2).setCellValue(dict.getDictType());
                row.createCell(3).setCellValue(dict.getDictName());
                row.createCell(4).setCellValue(dict.getFromType());
                row.createCell(5).setCellValue(dict.getDictDesc());
                row.createCell(6).setCellValue(dict.getGuidParents());
                row.createCell(7).setCellValue(dict.getDefaultValue());
                row.createCell(8).setCellValue(dict.getFromTable());
                row.createCell(9).setCellValue(dict.getUseForKey());
                row.createCell(10).setCellValue(dict.getUseForName());
                row.createCell(11).setCellValue(dict.getSqlFilter());
                row.createCell(12).setCellValue(dict.getSeqno().toString());
                j++;
            }

            XSSFSheet sheet1 = wb.createSheet("业务字典项");   //--->创建了一个工作簿

            sheet1.setColumnWidth((short)0, 30* 256);    //---》设置单元格宽度，因为一个单元格宽度定了那么下面多有的单元格高度都确定了所以这个方法是sheet的
            sheet1.setColumnWidth((short)1, 30* 256);    //---》设置单元格宽度，因为一个单元格宽度定了那么下面多有的单元格高度都确定了所以这个方法是sheet的
            sheet1.setColumnWidth((short)2, 60* 256);    //---》设置单元格宽度，因为一个单元格宽度定了那么下面多有的单元格高度都确定了所以这个方法是sheet的
            sheet1.setColumnWidth((short)3, 20* 256);    //---》设置单元格宽度，因为一个单元格宽度定了那么下面多有的单元格高度都确定了所以这个方法是sheet的
            sheet1.setColumnWidth((short)4, 20* 256);    //--->第一个参数是指哪个单元格，第二个参数是单元格的宽度
            sheet1.setColumnWidth((short)5, 20* 256);    //--->第一个参数是指哪个单元格，第二个参数是单元格的宽度
            sheet1.setDefaultRowHeight((short)300);    // ---->设置统一单元格的高度，就用这个方法

            //表格第一行
            XSSFRow row2 = sheet1.createRow(0);   //--->创建一行
            String[] columnList1 = {
                    SysDictItem.COLUMN_GUID, SysDictItem.COLUMN_GUID_DICT, SysDictItem.COLUMN_ITEM_NAME,
                    SysDictItem.COLUMN_ITEM_VALUE, SysDictItem.COLUMN_SEND_VALUE, SysDictItem.COLUMN_SEQNO};
            for (short i = 0; i < columnList1.length; i++) {
                row2.createCell(i).setCellValue(columnList1[i]);//--->创建单元格
            }
            int k = 1;
            for (SysDictItem dict : sysDictItems) {
                XSSFRow row = sheet1.createRow(k);   //--->创建一行
                row.createCell(0).setCellValue(dict.getGuid());
                row.createCell(1).setCellValue(dict.getGuidDict());
                row.createCell(2).setCellValue(dict.getItemName());
                row.createCell(3).setCellValue(dict.getItemValue());
                row.createCell(4).setCellValue(dict.getSendValue());
                row.createCell(5).setCellValue(dict.getSeqno().toString());
                k ++;
            }
            out = response.getOutputStream();
            wb.write(out);
            //样式1
//            XSSFCellStyle style = wb.createCellStyle(); // 样式对象
//            style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);// 垂直
//            style.setAlignment(XSSFCellStyle.ALIGN_CENTER);// 水平
//            //设置标题字体格式
//            Font font = wb.createFont();
//            //设置字体样式
//            font.setFontHeightInPoints((short)20);   //--->设置字体大小
//            font.setFontName("Courier New");   //---》设置字体，是什么类型例如：宋体
//            font.setItalic(true);     //--->设置是否是加粗
//
//            XSSFCellStyle style1 = wb.createCellStyle(); // 样式对象
//            style1.setFont(font);     //--->将字体格式加入到style1中
//
//
//            XSSFCell cell1 = row1.createCell((short)0);   //--->创建一个单元格
//            cell1.setCellStyle(style);
//            cell1.setCellValue("");

        } catch (ToolsRuntimeException e) {
            AjaxUtils.ajaxJsonErrorMessage(response,e.getCode(), e.getMessage());
            logger.error("exportDictExcel exception : ", e);
        }catch (Exception e) {
            AjaxUtils.ajaxJsonErrorMessage(response,"SYS_0001", e.getMessage());
            logger.error("exportDictExcel exception : ", e);
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {}
        }
    }
    
    /**
     * 查询业务字典对应树结构
     */
    @ResponseBody
    @RequestMapping(value="/queryDictTree", produces = "application/json;charset=UTF-8", method=RequestMethod.POST)
    public Map<String, Object> queryDictTree(@RequestBody String content) {
        Map<String, Object> result = new HashMap<>();
        JSONObject jsonObj = JSONObject.parseObject(content);
        String id = jsonObj.getString("id");
        String dictKey = jsonObj.getString("dictKey");
        //通过id判断需要加载的节点
        if("#".equals(id)){
            SysDict dict = dictRService.queryDict(dictKey);
            Map map=new HashMap();
            map.put("rootName", dict.getDictName());
            map.put("itemType", "dict");
            map.put("itemValue", dictKey);
            map.put("defaultValue", dict.getDefaultValue());
            map.put("dictguid", dict.getGuid());
            result.put("data", map);//返回给前台的数据
        } else {
            List<SysDictItem> sysDictItems = dictRService.queryDictItemListByDictKey(dictKey);
            result.put("data", sysDictItems);//返回给前台的数据
        }
        return getReturnMap(result.get("data"));
    }

    
    /**
     * 根据key查询业务字典信息
     *
     */
    @ResponseBody
    @RequestMapping(value="/queryDict", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public Map<String, Object> queryDict(@RequestBody String content) {
        JSONObject jsonObject= JSONObject.parseObject(content);
        String dictKey = jsonObject.getString("dictKey");
        SysDict dict = dictRService.queryDict(dictKey);
        return getReturnMap(dict);
    }



    /**
     * 修改字典项默认值
     *
     */
    @OperateLog(
            operateType = JNLConstants.OPEARTE_TYPE_UPDATE,
            operateDesc = "设置字典默认字典项",
            retType = ReturnType.Object,
            id = "dictKey",
            name = "dictName",
            keys = "defaultValue"
    )
    @ResponseBody
    @RequestMapping(value="/setDefaultDictValue", produces = "application/json;charset=UTF-8", method= RequestMethod.POST)
    public Map<String, Object> setDefaultDictValue(@RequestBody String content) {
        JSONObject obj = JSONObject.parseObject(content);
        String dictKey = obj.getString("dictKey");
        String itemValue = obj.getString("itemValue");
        return getReturnMap(dictRService.setDefaultDictValue(dictKey, itemValue));
    }

}
