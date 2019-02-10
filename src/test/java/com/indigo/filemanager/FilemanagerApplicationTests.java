package com.indigo.filemanager;

import com.indigo.filemanager.service.FileTransferService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FilemanagerApplicationTests {

	@Autowired
	private FileTransferService fileTransferService;
	@Test
	public void contextLoads() {
	}

	@Test
	public void convert() throws IOException {
//		BufferedInputStream bis = new BufferedInputStream(new FileInputStream("E:\\dgy\\projects\\filemanager\\filemanager\\123.doc"));
//		InputStream in = new FileInputStream(new File("E:\\dgy\\projects\\filemanager\\filemanager\\123.doc"));
		InputStream inputStream = new FileInputStream("E:\\dgy\\projects\\filemanager\\filemanager\\123.doc");
		OutputStream outputStream=fileTransferService.transferPdf(inputStream,"doc","pdf");
		outputStream.close();
	}

}

