package org.isf.shared.mapper.converter;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.AbstractConverter;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;

/**
 * @author akashytsa
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
