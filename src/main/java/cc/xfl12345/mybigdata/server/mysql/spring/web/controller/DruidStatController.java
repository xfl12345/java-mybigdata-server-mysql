package cc.xfl12345.mybigdata.server.mysql.spring.web.controller;

import com.alibaba.druid.stat.DruidStatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequestMapping(DruidStatController.servletName)
public class DruidStatController {

    protected DruidStatService statService = DruidStatService.getInstance();
    public static final String servletName = "druid";
    public static final String servletPathCache1 = "/" + servletName;

    @RequestMapping("{partOne:^\\w+.+}.json")
    public @ResponseBody String root(HttpServletRequest request, @PathVariable String partOne) {
        String relativeURL = request.getServletPath().substring(servletPathCache1.length());
        String httpGetQueryString = request.getQueryString();
        if(httpGetQueryString != null && !httpGetQueryString.isEmpty()) {
            relativeURL += '?' + httpGetQueryString;
        }
        return statService.service(relativeURL);
    }

    @RequestMapping(value = {
        "weburi-/{partOne:.*}.json",
        "weburi-/*/{partOne:.*}.json",
        "weburi-/*/*/{partOne:.*}.json",
        "weburi-/*/*/*/{partOne:.*}.json",
        "weburi-/*/*/*/*/{partOne:.*}.json",
        "weburi-/*/*/*/*/*/{partOne:.*}.json"
    })
    public @ResponseBody String weburi(HttpServletRequest request, @PathVariable String partOne) {
        return root(request, partOne);
    }
}
