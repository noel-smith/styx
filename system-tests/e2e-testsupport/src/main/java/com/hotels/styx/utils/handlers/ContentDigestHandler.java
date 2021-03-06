/*
  Copyright (C) 2013-2018 Expedia Inc.

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
package com.hotels.styx.utils.handlers;

import com.hotels.styx.api.HttpRequest;
import com.hotels.styx.api.HttpResponse;
import com.hotels.styx.api.LiveHttpRequest;
import com.hotels.styx.api.LiveHttpResponse;
import com.hotels.styx.api.extension.Origin;
import com.hotels.styx.common.http.handler.BaseHttpHandler;
import reactor.core.publisher.Mono;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.google.common.net.MediaType.HTML_UTF_8;
import static com.hotels.styx.api.HttpResponseStatus.OK;
import static java.lang.String.format;
import static java.util.UUID.randomUUID;

public class ContentDigestHandler extends BaseHttpHandler {
    private final Origin origin;

    public ContentDigestHandler(Origin origin) {
        this.origin = origin;
    }

    @Override
    protected LiveHttpResponse doHandle(LiveHttpRequest request) {
        HttpRequest fullRequest = Mono.from(request.aggregate(0x100000)).block();

        String responseBody = format("Response From %s - %s, received content digest: %s",
                origin.hostAndPortString(),
                randomUUID(),
                fullRequest.bodyAs(UTF_8).hashCode());

        return HttpResponse.response(OK)
                .header(CONTENT_TYPE, HTML_UTF_8.toString())
                .header(CONTENT_LENGTH, responseBody.getBytes(UTF_8).length)
                .body(responseBody, UTF_8)
                .build()
                .stream();
    }
}
