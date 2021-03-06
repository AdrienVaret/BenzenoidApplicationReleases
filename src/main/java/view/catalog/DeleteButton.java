package view.catalog;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import utils.Utils;

public class DeleteButton extends Button {

	@SuppressWarnings("unused")
	private CatalogPane parent;

	@SuppressWarnings("unused")
	private int index;

	public DeleteButton(CatalogPane parent, int index) {
		this.parent = parent;
		this.index = index;

		Image image;

		if (Utils.onWindows())
			image = new Image("file:graphics\\close_button.png");
		else
			image = new Image("file:graphics/close_button.png");

		ImageView view = new ImageView(image);

		this.resize(30, 30);

		this.setPadding(new Insets(0));

		this.setGraphic(view);

		this.setOnAction(e -> {
			parent.removeEntry(index);
		});

	}

}
