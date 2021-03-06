package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.gson.Gson;

public class Post {

	@SuppressWarnings("rawtypes")
	public static List<Map> post(String urlString, String jsonInputString) throws IOException {

		URL url = new URL(urlString);

		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");

		con.setRequestProperty("Content-Type", "application/json; utf-8");
		con.setRequestProperty("Accept", "application/json");
		con.setDoOutput(true);

		try (OutputStream os = con.getOutputStream()) {
			byte[] input = jsonInputString.getBytes("utf-8");
			os.write(input, 0, input.length);
		}

		try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
			StringBuilder response = new StringBuilder();
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}

			System.out.println(response.toString());

			if (!response.toString().equals("[]")) {

				String res = response.toString();
				res = res.substring(2, res.length() - 2);

				String[] results = res.split(Pattern.quote("},{"));

				List<Map> maps = new ArrayList<>();

				for (int i = 0; i < results.length; i++) {
					results[i] = "{" + results[i] + "}";
					Gson gson = new Gson();
					Map map = gson.fromJson(results[i], Map.class);
					maps.add(map);
				}

				br.close();
				con.disconnect();

				return maps;

			}

			else
				return new ArrayList<>();
		}
	}
}
