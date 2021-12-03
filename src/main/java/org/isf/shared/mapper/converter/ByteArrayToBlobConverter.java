/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2020 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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

import javax.sql.rowset.serial.SerialBlob;

import org.modelmapper.AbstractConverter;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

/**
 * @author akashytsa
 */
@Component
public class ByteArrayToBlobConverter extends AbstractConverter<byte[], Blob> {

	private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(ByteArrayToBlobConverter.class);

	@Override
	protected Blob convert(byte[] data) {
        try {
            return new SerialBlob(data);
        } catch (SQLException e) {
            LOGGER.error("", e);
        }
        return null;
    }
}
