(function($){
	var initLayout = function() {
		//var hash = window.location.hash.replace('#', '');
		//var currentTab = $('ul.navigationTabs a')
		//					.bind('click', showTab)
		//					.filter('a[rel=' + hash + ']');
		//if (currentTab.size() == 0) {
		//	currentTab = $('ul.navigationTabs a:first');
		//}
		//showTab.apply(currentTab.get(0));
		//$('#colorpickerHolder').ColorPicker({flat: true});
		var rgbTextBox = $('#rgb');
		var initialColour = rgbTextBox.prop('value');

		if (initialColour == '') {
			initialColour = '00ff00';
		}
		
		$('#colorpickerHolder2').ColorPicker({
			flat: true,
			color: '#' + initialColour,
			onSubmit: function(hsb, hex, rgb) {
				// set the background colour of the selector
				$('#colorSelector2 div').css('backgroundColor', '#' + hex);
				// set the rgb value to be stored in the db
				$(rgbTextBox).val(hex);
			}
		});
		// set the stored colour
		$('#colorSelector2 div').css('backgroundColor', '#' + initialColour)
		// set the rgb value to be stored in the db 
		$(rgbTextBox).val(initialColour);
		
		$('#colorpickerHolder2>div').css('position', 'absolute');
		var widt = false;
		$('#colorSelector2').bind('click', function() {
			$('#colorpickerHolder2').stop().animate({height: widt ? 0 : 173}, 500);
			widt = !widt;
		});
		/*
		$('#colorpickerField1, #colorpickerField2, #colorpickerField3').ColorPicker({
			onSubmit: function(hsb, hex, rgb, el) {
				$(el).val(hex);
				$(el).ColorPickerHide();
			},
			onBeforeShow: function () {
				$(this).ColorPickerSetColor(this.value);
			}
		})
		.bind('keyup', function(){
			$(this).ColorPickerSetColor(this.value);
		});
		*/
		/*
		$('#colorSelector').ColorPicker({
			color: '#0000ff',
			onShow: function (colpkr) {
				$(colpkr).fadeIn(500);
				return false;
			},
			onHide: function (colpkr) {
				$(colpkr).fadeOut(500);
				return false;
			},
			onChange: function (hsb, hex, rgb) {
				$('#colorSelector div').css('backgroundColor', '#' + hex);
			}
		});
		*/
	};
	
	/*
	var showTab = function(e) {
		var tabIndex = $('ul.navigationTabs a')
							.removeClass('active')
							.index(this);
		$(this)
			.addClass('active')
			.blur();
		$('div.tab')
			.hide()
				.eq(tabIndex)
				.show();
	};
	*/
	
	EYE.register(initLayout, 'init');
})(jQuery)