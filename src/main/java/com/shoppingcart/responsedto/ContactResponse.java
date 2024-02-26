package com.shoppingcart.responsedto;

import com.shoppingcart.enums.Priority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactResponse {
	private int contactId;
	private String name;
	private long contactNumber;
	private String priority;
}
