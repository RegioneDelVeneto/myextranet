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

import it.regioneveneto.myp3.mychannel.client.exception.MyChannelException;
import it.regioneveneto.myp3.mychannel.client.model.MessageDetails;
import it.regioneveneto.myp3.myextranet.service.MyChannelService;

@Service
public class MyChannelServiceImpl implements MyChannelService {
	
	private final String ipa;
	
	private final it.regioneveneto.myp3.mychannel.client.MyChannelService myChannelClient;

	public MyChannelServiceImpl(
			@Value("${mychannel.baseUrl}") String myChannelUrl, 
			@Value("${myportal.ipa}")String ipa) {
		super();
		this.ipa = ipa;
		this.myChannelClient = new it.regioneveneto.myp3.mychannel.client.MyChannelService(myChannelUrl);
	}

	@Override
	public void sendEmail(MessageDetails details) throws MyChannelException {
		
		myChannelClient.sendEmail(details, ipa);
	}
}
