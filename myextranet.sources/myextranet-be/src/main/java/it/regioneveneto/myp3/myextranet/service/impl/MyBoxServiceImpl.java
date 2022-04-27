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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.regioneveneto.myp3.mybox.BoxRepository;
import it.regioneveneto.myp3.mybox.BoxRepositoryCreationException;
import it.regioneveneto.myp3.mybox.BoxRepositoryFactory;
import it.regioneveneto.myp3.mybox.ContentMetadata;
import it.regioneveneto.myp3.mybox.RepositoryAccessException;
import it.regioneveneto.myp3.myextranet.exception.MyExtranetException;
import it.regioneveneto.myp3.myextranet.service.MyBoxService;

@Service
public class MyBoxServiceImpl implements MyBoxService {
	
	private static final Logger logger = LoggerFactory.getLogger(MyBoxServiceImpl.class);
	
	private final String bucket;
	private final String myboxDownloadContext;

	@Value("${mybox.configPath}") String myBoxConfigurationFilePath;		
	BoxRepository boxRepository;
	
	
	
	public MyBoxServiceImpl(
			@Value("${mybox.bucket}")String bucket,
			@Value("${mybox.download.context}")String myboxDownloadContext
			) throws MyExtranetException  {
		this.bucket = bucket;
		this.myboxDownloadContext = myboxDownloadContext;
	}
	
	private void ensureRepository() throws IOException, BoxRepositoryCreationException {
	    File configurationFile = new File(this.myBoxConfigurationFilePath);

	    if (configurationFile.exists()) {
	      InputStream stream = null;
	      Exception throwable = null;

	      try {
	        Properties properties = new Properties();
	        stream = new FileInputStream(configurationFile);
	  
	        properties.load(stream);
	  
	        boxRepository = BoxRepositoryFactory.getInstance(properties);  
	      } catch (Exception e) {
	        throwable = e;
	      } finally {
	        IOUtils.closeQuietly(stream);
	      }

	      if (throwable != null)
	        throw new RuntimeException(throwable);
	    }
	    else
	      boxRepository = BoxRepositoryFactory.getInstance();
	  }

	@Override
	public String storeFileContents(InputStream data, ContentMetadata fileMetadata) throws MyExtranetException  {
		
		String contentId;
		try {
			ensureRepository();
			contentId = boxRepository.put(bucket, data, fileMetadata);
		} catch (RepositoryAccessException e) {
			throw new MyExtranetException("Error inserting content in MyBox repository for IPA " + bucket, e);
		} catch (IOException e) {
			
			logger.error("Error getting a MyBox repository instance", e);
			throw new MyExtranetException("Error getting a MyBox repository instance", e);
		} catch (BoxRepositoryCreationException e) {
			
			logger.error("Error getting a MyBox repository instance", e);
			throw new MyExtranetException("Error getting a MyBox repository instance", e);
		}
		
		return contentId;
	}
	
	@Override
	public InputStream getFileContents(String contentId) throws MyExtranetException  {
		try {
			ensureRepository();
			return boxRepository.get(bucket, contentId);
		} catch (RepositoryAccessException e) {
			throw new MyExtranetException(String.format("Error getting content from MyBox repository (ipa = %s, contentId = %s)", bucket, contentId), e);
		} catch (IOException e) {
			
			logger.error("Error getting a MyBox repository instance", e);
			throw new MyExtranetException("Error getting a MyBox repository instance", e);
		} catch (BoxRepositoryCreationException e) {
			
			logger.error("Error getting a MyBox repository instance", e);
			throw new MyExtranetException("Error getting a MyBox repository instance", e);
		}
		
	}
	
	@Override
	public ContentMetadata getFileMetadata(String contentId) throws MyExtranetException {
		try {
			ensureRepository();
			return boxRepository.getMetadata(bucket, contentId);
		} catch (RepositoryAccessException e) {
			throw new MyExtranetException(String.format("Error getting content metadata from MyBox repository (ipa = %s, contentId = %s)", bucket, contentId), e);
		}catch (IOException e) {
			
			logger.error("Error getting a MyBox repository instance", e);
			throw new MyExtranetException("Error getting a MyBox repository instance", e);
		} catch (BoxRepositoryCreationException e) {
			
			logger.error("Error getting a MyBox repository instance", e);
			throw new MyExtranetException("Error getting a MyBox repository instance", e);
		}
	}

	@Override
	public String buildDownloadUrl(String logoContentId) {
		return this.myboxDownloadContext + logoContentId;
	}

	@Override
	public void cleanMetadataFileName(ContentMetadata metadata) {
		if (metadata == null) return;
		
		String fn = metadata.getFileName();
		int idx = fn.lastIndexOf("_");
		int idxDot = fn.lastIndexOf(".");
		
		if (idx <= idxDot) return;
		
		metadata.setFileName(fn.substring(0, idx));
		
	}

}
