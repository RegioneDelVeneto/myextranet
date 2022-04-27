/**
 *     MyExtranet, il portale per collaborare con l’ente Regione Veneto.
 *     Copyright (C) 2022  Regione Veneto
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package it.regioneveneto.myp3.myextranet.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.markusbernhardt.proxy.ProxySearch;
import com.github.markusbernhardt.proxy.ProxySearch.Strategy;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.regioneveneto.myp3.myconfig.client.MyConfigClient;
import it.regioneveneto.myp3.myconfig.client.models.ConfItemDto;
import it.regioneveneto.myp3.myextranet.service.MyConfigService;

@Service
public class MyConfigServiceImpl implements MyConfigService {
	
	private static final Logger logger = LoggerFactory.getLogger(MyConfigServiceImpl.class);


	private MyConfigClient myConfigClient;
	
	public MyConfigServiceImpl(
			@Value("${myconfig.baseUrl}") String myconfigBaseUrl
			) {
		super();
		
		this.myConfigClient = new MyConfigClient(myconfigBaseUrl);
	}

	@Override
	public ConfItemDto getItem(String code) {
		try {
			String encodedCode = encodeValue(code);
			ConfItemDto configItem = this.myConfigClient.getSimpleItem(encodedCode);
			return configItem;
		} catch (Exception e) {
			logger.debug(String.format("Item with code \"%s\" not found ", code));
			return null;
		}
		
	}
	
	@Override
	public String getConcatValue(String code, String separator) {
		ConfItemDto item = getItem(code);
		
		try {
			if (item == null) return "";
			
			String encoded = String.join(separator, item.getValue());
			return URLDecoder.decode(encoded, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private String encodeValue(String value) throws UnsupportedEncodingException {
	    return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
	}

	
}
