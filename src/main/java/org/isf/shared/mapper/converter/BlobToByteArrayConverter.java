package org.isf.shared.mapper.converter;

import org.modelmapper.AbstractConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Blob;
import java.sql.SQLException;

/**
 * @author akashytsa
 *
 */
@Component
public class BlobToByteArrayConverter extends AbstractConverter<Blob, byte[]> {

    private final Logger logger = LoggerFactory.getLogger(BlobToByteArrayConverter.class);

	@Override
	protected byte[] convert(Blob data){
        byte[] blobAsBytes = new byte[0];
        try {
            if(data != null) {
                int blobLength = (int) data.length();
                blobAsBytes = data.getBytes(1, blobLength);
                data.free();
            }
        } catch (SQLException e) {
            logger.error("", e);
        }
        return blobAsBytes;
	}

}
