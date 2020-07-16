package org.isf.sms.mapper;

import org.isf.shared.GenericMapper;
import org.isf.sms.dto.SmsDTO;
import org.isf.sms.model.Sms;
import org.springframework.stereotype.Component;

@Component
public class SmsMapper extends GenericMapper<Sms, SmsDTO>{
	public SmsMapper() {
		super(Sms.class, SmsDTO.class);
	}
}
