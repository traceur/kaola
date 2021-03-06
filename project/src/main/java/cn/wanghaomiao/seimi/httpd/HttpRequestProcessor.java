package cn.wanghaomiao.seimi.httpd;
/*
   Copyright 2015 - now original author

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */


import cn.wanghaomiao.seimi.core.SeimiQueue;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 汪浩淼 et.tw@163.com
 * @since 2015/11/19.
 */
public abstract class HttpRequestProcessor {
    protected SeimiQueue seimiQueue;
    protected String crawlerName;

    public HttpRequestProcessor(SeimiQueue seimiQueue, String crawlerName) {
        this.seimiQueue = seimiQueue;
        this.crawlerName = crawlerName;
    }

    public abstract void handleHttpRequest(HttpServletRequest request, HttpServletResponse response)throws IOException, ServletException;
}
