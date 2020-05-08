package com.seattleacademy.team20;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Handles requests for the application home page.
 */
 @Controller
public class SkillController {

    private static final Logger logger = LoggerFactory.getLogger(SkillController.class);

    @RequestMapping(value = "/skill-upload", method = RequestMethod.GET)
    public String upload(Locale locale, Model model) {
        logger.info("Welcome home! The client locale is {}.", locale);

        try {
			initialize();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
        List<skill> skills = selectskills();
		uploadSkill(skills);

        return "skillupload";
    }


    //①MySQLからデータを取ってくる
    @Autowired
	private JdbcTemplate jdbcTemplate;
	public List<skill> selectskills(){
		final String sql = "select * from skills";
		return jdbcTemplate.query(sql, new RowMapper<skill>() {
			public skill mapRow(ResultSet result, int rowNum) throws SQLException{
				return new skill(
					result.getString("Category"),
					result.getString("name"),
					result.getInt("score")
				);
			}
		});
	}

	//②Firebaseの初期化
	private FirebaseApp app;
	public void initialize() throws IOException {
	FileInputStream refreshToken = new FileInputStream("/Users/yurino/seattle-test/developid.json");
	FirebaseOptions options = new FirebaseOptions.Builder()
		    .setCredentials(GoogleCredentials.fromStream(refreshToken))
		    .setDatabaseUrl("https://develop-754a7.firebaseio.com/")
		    .build();
		app = FirebaseApp.initializeApp(options , "other");
	}

	//③Realtimedatabaseにデータを送る
	public void uploadSkill(List<skill> skills) {
		// 1.データの保存
		final FirebaseDatabase database = FirebaseDatabase.getInstance(app);
		DatabaseReference ref = database.getReference("skills");

		// 2.Realtimedatabaseの形変更する
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
 		Map<String, Object> map;
 		Map<String, List<skill>> skillMap = skills.stream().collect(Collectors.groupingBy(skill::getName));
 		for (Map.Entry<String, List<skill>> entry : skillMap.entrySet()) {
 			map = new HashMap<>();
 			map.put("skillCategory", entry.getValue());
 			dataList.add(map);
 		}

		//④データ取得できたかわかるようにしておく
		ref.setValue(dataList, new DatabaseReference.CompletionListener() {
			@Override
			public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
				if(databaseError != null) {
					System.out.println("Data could not be saved"+ databaseError.getMessage());
				}else {
					System.out.println("Data saved successfuly");
				}
			}
		});
	}
 }