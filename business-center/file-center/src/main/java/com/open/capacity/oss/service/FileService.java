package com.open.capacity.oss.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.open.capacity.common.web.PageResult;
import com.open.capacity.oss.model.FileInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 作者 owen 
 * @version 创建时间：2017年11月12日 上午22:57:51
 * 文件service 目前仅支持阿里云oss,七牛云
*/
public interface FileService {

	FileInfo upload(MultipartFile file ) throws Exception;

	void delete(FileInfo fileInfo);
	
	FileInfo getById(String id);
	
	PageResult<FileInfo>  findList(Map<String, Object> params);

	void unZip(String filePath, String descDir) throws RuntimeException ;

	void chunk(String guid, Integer chunk, MultipartFile file, Integer chunks,String filePath) throws Exception;

}
