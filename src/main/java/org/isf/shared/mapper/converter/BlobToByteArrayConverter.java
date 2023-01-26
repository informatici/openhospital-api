/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2022 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.shared.mapper.converter;

import java.sql.Blob;
import java.sql.SQLException;

import org.modelmapper.AbstractConverter;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

/**
 * @author akashytsa
 */
@Component
public class BlobToByteArrayConverter extends AbstractConverter<Blob, byte[]> {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(BlobToByteArrayConverter.class);

	@Override
	protected byte[] convert(Blob data){
        byte[] blobAsBytes = new byte[0];
        try {
            if (data != null) {
                int blobLength = (int) data.length();
                blobAsBytes = data.getBytes(1, blobLength);
                data.free();
            }
        } catch (SQLException e) {
            LOGGER.error("", e);
        }
        return blobAsBytes;
	}

}
