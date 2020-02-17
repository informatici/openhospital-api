package org.isf.shared.mapper.converter;

import org.modelmapper.AbstractConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * @author akashytsa
 *
 */
public class ByteArrayToBlobConverter extends AbstractConverter<byte[], Blob> {
    private final Logger logger = LoggerFactory.getLogger(ByteArrayToBlobConverter.class);

	@Override
	protected Blob convert(byte[] data) {
        try {
            return new SerialBlob(data);
        } catch (SQLException e) {
            logger.error("", e);
        }
        return null;
    }

}
