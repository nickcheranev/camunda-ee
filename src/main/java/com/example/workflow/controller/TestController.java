package com.example.workflow.controller;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("/Test/v1/start")
    @ApiOperation(value = "Test", response = String.class, tags = {"Test"})
    public ResponseEntity<String> start() {

        String data = "{\"sender\":\"MessageHubPackageGenerate\"," +
                "\"packageSource\":\"RssConsultant\"," +
                "\"businessKey\":\"package[1]-format[Консультант XML]-rule[Правило загрузки ручной ввод]\"," +
                "\"messageId\":\"-1494237524\"," +
                "\"packageContent\":" +
                "\"<rss version=\\\"2.0\\\">" +
                "<channel>" +
                "<title>КонсультантПлюс: Новое в российском законодательстве</title>" +
                "<description/>" +
                "<link>http://www.consultant.ru/law/review/</link>" +
                "<language>ru</language>" +
                "<pubDate>Sun, 19 Sep 2021 18:00:43 +0300</pubDate><lastBuildDate>Sun, 19 Sep 2021 18:00:43 +0300</lastBuildDate>" +
                "<image>" +
                "<link>http://www.consultant.ru/law/review/</link>" +
                "<url>http://www.consultant.ru/images/logo.gif</url><title>КонсультантПлюс: Новое в российском законодательстве" +
                "</title><width>144</width><height>55</height>" +
                "</image>" +
                "<item>" +
                "<title>Постановление Правительства РФ от 16.09.2021 N 1564 &quot;О переносе выходных дней в 2022 году&quot;</title>" +
                "<pubDate>Fri, 17 Sep 2021 12:00:00 +0300</pubDate>" +
                "<category>ТРУД И ЗАНЯТОСТЬ</category>" +
                "<link>http://www.consultant.ru/law/review/208044071.html#utm_campaign=rss_fd&amp;utm_source=rss_reader&amp;utm_medium=rss</link>" +
                "<guid>http://www.consultant.ru/law/review/208044071.html#utm_campaign=rss_fd&amp;utm_source=rss_reader&amp;utm_medium=rss</guid>" +
                "<description>Определены дни праздничного отдыха россиян в 2022 году С учетом нормы статьи 112 ТК РФ о переносе выходного дня на следующий после него рабочий день при совпадении выходного и нерабочего праздничного дней в 2022 году будут следующие дни отдыха: с 31 декабря...</description>" +
                "</item>" +
                "<item><title>&quot;Комплекс мер по повышению эффективности функционирования механизмов реализации, соблюдения и защиты прав и законных интересов детей, проживающих в детских домах-интернатах, а также детей, помещенных под надзор в организации для детей-сирот и детей, оставшихся без попечения родителей, в целях качественного улучшения их жизни&quot; (утв. Правительством РФ 09.09.2020 N 8379п-П12)</title>" +
                "<pubDate>Fri, 17 Sep 2021 12:00:00 +0300</pubDate>" +
                "<category>СОЦИАЛЬНОЕ ОБЕСПЕЧЕНИЕ. ПОСОБИЯ И ЛЬГОТЫ</category>" +
                "<link>http://www.consultant.ru/law/review/208044072.html#utm_campaign=rss_fd&amp;utm_source=rss_reader&amp;utm_medium=rss</link>" +
                "<guid>http://www.consultant.ru/law/review/208044072.html#utm_campaign=rss_fd&amp;utm_source=rss_reader&amp;utm_medium=rss</guid>" +
                "<description>Установлен комплекс мер по повышению эффективности защиты прав и законных интересов детей-сирот и детей, оставшихся без попечения родителей Документом предусмотрено осуществление ряда мероприятий, в том числе, в области медицины, образования, реабилитации...</description>" +
                "</item>" +
                "</channel>" +
                "</rss>\"" +
                // ,"\"correlationId\":\"6d38f4c2-efbf-4128-9303-09ab6383c614\"" +
                "}";

        kafkaTemplate.send("from-message-hub", data);
        return ResponseEntity.ok("OK");
    }
}
