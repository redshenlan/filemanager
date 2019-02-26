/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.indigo.filemanager.controller;

import com.indigo.filemanager.bus.service.FileTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@RestController
public class OutController {
    @Autowired
    private FileTransferService fileTransferService;

	/**
	 * 在线查看
	 * @param filekey
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/files/view/{filekey}")
	public String viewfile(@PathVariable("filekey") String filekey, HttpServletResponse response) throws IOException {
		InputStream is = new FileInputStream("d:\\1234ttp.pdf");
		OutputStream ops=response.getOutputStream();
		response.setContentType("application/pdf");
		 ops.flush();
		if (is != null) {
			byte buff[] = new byte[1048576];
			for (int byteread = 0; (byteread = is.read(buff)) != -1; ) {
				ops.write(buff, 0, byteread);
				ops.flush();
			}

		}
		return null;
	}
	@RequestMapping("/testTransfer")
	public void testTransfer() throws IOException {
		InputStream inputStream = new FileInputStream("D:\\ftp\\1546422516097.doc");
		OutputStream outputStream=fileTransferService.transferPdf(inputStream,"doc","pdf");
		outputStream.close();
	}

}
