package org.isf.shared.mapper.converter;

import java.sql.Blob;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;

import org.modelmapper.AbstractConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author akashytsa
 *
 */
@Slf4j
public class ByteArrayToBlobConverter extends AbstractConverter<byte[], Blob> {
	
	@Override
	protected Blob convert(byte[] data) {
        try {
            return new SerialBlob(data);
        } catch (SQLException e) {
            log.error("", e);
        }
        return null;
    }
}
