package com.indigo.filemanager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.indigo.filemanager.bus.domain.UserRepository;
import com.indigo.filemanager.bus.domain.entity.User;
import com.indigo.filemanager.bus.service.FileManager;
import com.indigo.filemanager.bus.service.FileTransferService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FilemanagerApplicationTests {

	@Autowired
	private FileTransferService fileTransferService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private FileManager fileManager;
	@Test
	public void contextLoads() {
	}

	@Test
	public void convert() throws IOException {
//		BufferedInputStream bis = new BufferedInputStream(new FileInputStream("E:\\dgy\\projects\\filemanager\\filemanager\\123.doc"));
//		InputStream in = new FileInputStream(new File("E:\\dgy\\projects\\filemanager\\filemanager\\123.doc"));
		InputStream inputStream = new FileInputStream("D:\\ftp\\1546422516097.doc");
		OutputStream outputStream=fileTransferService.transferPdf(inputStream,"doc","pdf");
		outputStream.close();
//		outputStream.close();
	}
	
	@Test
	public void fileManagerTest() throws IOException {
		User user = userRepository.findByAccessKeyIdAndValid("010101","Y");
		InputStream is = fileManager.getFileFdf("1c5e692206ef415a89a44f08b0ea39a1", user);
		System.out.println(is.available());
	}
	
}

