package com.github.memos.utils;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;

@Slf4j
public class CNCityCrawler {

    public static void main(String[] args) throws IOException {
        int num = 0;
        //创建文件输出流
        FileWriter fileWriter = new FileWriter("xxx.sql");
        //1_设置访问url(国际统计局的统计地址)
        String baseUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2021/";
        //使用jsoup获取页面内容
        Document doc = getPersonalDoc(baseUrl);
        //获取页面中<tr>标签且class = "provincetr" ,然后查询所有的a标签元素
        Elements provinceTds = doc.select("tr[class=provincetr]").select("a");
        String cityName;
        String cityName0 = "";
        String cityName1 = "";
        String cityName2 = "";
        String cityName3 = "";
        String cityName4 = "";
        String parentCode;
        String baseFormat = "insert into area (id,name,level,pid,fullname) values ({0},{1},{2},{3},{4});";
        
        //遍历数据
        for (Element element : provinceTds) {
            //获取标签文本内容
            cityName = element.text();
            cityName0 = element.text();
            //获取省级code
            String href = element.attr("href");
            int index = href.indexOf(".html");
            String provinceCode = href.substring(0, index);
            parentCode = "0";
            String provincePath = "''";
            //格式化需要输出的字符串
            String format = MessageFormat.format(baseFormat, provinceCode, "'" + cityName + "'", 1, parentCode, "'" + cityName0 + "'");
            //输出流输出内容并刷新
            fileWriter.write(format);
            fileWriter.write("\r\n");
            fileWriter.flush();
            log.info(format);
            
            //2_获取市级访问url
            String cityUrl = baseUrl + href;
            doc = getPersonalDoc(cityUrl);
            //获取页面中<tr>标签且class = "citytr"
            Elements cityTds = doc.select("tr[class=citytr]");
            for (Element cityTd : cityTds) {
                //获取元素中的所有a标签元素
                Elements tds = cityTd.select("a");
                //获取城市名
                cityName = tds.get(1).text();
                cityName1 = cityName0 + "，" + tds.get(1).text();
                parentCode = provinceCode;
                //获取城市代码
                String cityCode = tds.get(0).text();
                //获取a标签的访问链接
                if (tds.size() == 0) {
                    continue;
                }
                href = tds.get(0).attr("href");
                //格式化字符串
                format = MessageFormat.format(baseFormat, cityCode, "'" + cityName + "'", 2, parentCode, "'" + cityName1 + "'");
                fileWriter.write(format);
                fileWriter.write("\r\n");
                fileWriter.flush();
                log.info(format);

                //3_获取县级访问url
                String countryUrl = baseUrl + href;
                doc = getPersonalDoc(countryUrl);
                Elements countryTds = doc.select("tr[class=countytr]");
                for (Element countryTd : countryTds) {
                    Elements tds1 = countryTd.select("a");
                    if (tds1.size() == 0) {
                        continue;
                    }
                    href = tds1.get(0).attr("href");
                    tds = countryTd.select("td");
                    cityName = tds.get(1).text();
                    cityName2 = cityName1 + "，" + tds.get(1).text();
                    parentCode = cityCode;
                    String parentPath = provinceCode + "," + parentCode;
                    String countryCode = tds.get(0).text();
                    format = MessageFormat.format(baseFormat, countryCode, "'" + cityName + "'", 3, parentCode, "'" + cityName2 + "'");
                    fileWriter.write(format);
                    fileWriter.write("\r\n");
                    fileWriter.flush();
                    log.info(format);


                    //4_获取乡级访问url
                    String townUrl = baseUrl + "/" + provinceCode + "/" + href;
                    doc = getPersonalDoc(townUrl);
                    Elements townTds = doc.select("tr[class=towntr]");
                    for (Element townTd : townTds) {
                        Elements tds2 = townTd.select("a");
                        if (tds2.size() == 0) {
                            continue;
                        }
                        href = tds2.get(0).attr("href");
                        tds = townTd.select("td");
                        cityName = tds.get(1).text();
                        cityName3 = cityName2 + "，" + tds.get(1).text();
                        parentCode = countryCode;
                        String parent1Path = provinceCode + "," + parentCode;
                        String townCode = tds.get(0).text();
                        format = MessageFormat.format(baseFormat, townCode, "'" + cityName + "'", 4, parentCode, "'" + cityName3 + "'");
                        fileWriter.write(format);
                        fileWriter.write("\r\n");
                        fileWriter.flush();
                        log.info(format);

                        //5_获取村级访问url
                        String streetUrl = baseUrl + "/" + townCode.substring(0, 2) + "/" + townCode.substring(2, 4) + "/" + href;
                        doc = getPersonalDoc(streetUrl);
                        Elements streetTds = doc.select("tr[class=villagetr]");
                        for (Element streetTd : streetTds) {
                            tds = streetTd.select("td");
                            cityName = tds.get(2).text();
                            cityName4 = cityName3 + "，" + tds.get(2).text();
                            parentCode = townCode;
                            String parent2Path = provinceCode + "," + parentCode;
                            String streetCode = tds.get(0).text();
                            format = MessageFormat.format(baseFormat, streetCode, "'" + cityName + "'", 5, parentCode, "'" + cityName4 + "'");
                            fileWriter.write(format);
                            fileWriter.write("\r\n");
                            fileWriter.flush();
                            log.info(format);
                        }
                        num += streetTds.size();
                        log.info("辖区下居委会数" + streetTds.size());
                        log.info("累加" + num);
                        
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("合计:" + num);
        fileWriter.close();
    }

    public static Document getPersonalDoc(String url) {
        Document doc = null;
        while (true) {
            try {
                doc = Jsoup.connect(url).timeout(1000).get();
                break;
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return doc;
    }

}
