package com.kh.spring.memo.model.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//lombok의 @Data
//Equivalent to @Getter @Setter @RequiredArgsConstructor @ToString @EqualsAndHashCode. 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Memo {
	private int no;
	private String memo;
	private String password;
	private Date regDate;
	
	
}
