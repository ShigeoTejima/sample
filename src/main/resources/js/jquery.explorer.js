'use strict';

$.explorer = $.explorer || {};

$.extend($.explorer, {
	version: "0.0.1",

	filetypes: (() => {
		const filetypes = {
			"folder": "ファイル フォルダー",
			"png"   : "PNG ファイル",
			"txt"   : "テキスト ドキュメント",
			""      : "ファイル"
		};
		return filetypes;
	})(),
	Util: (() => {
		class Util {
			static leftPad(value, length, padChar = ' ') {
				var convert = (!value) ? "" : value.toString();
				for (; convert.length < length; convert = padChar + convert) {} 
				return convert;
			}
			static formatDate(date) {
				if (!date) {
					return;
				}
				if (toString.call(date) !== "[object Date]") {
					throw "date must be Date";
				}

				var year = date.getFullYear();
				var month = date.getMonth() + 1;
				var day = date.getDate();
				var hour = date.getHours();
				var minute = date.getMinutes();
				var second = date.getSeconds();

				var leftPad = $.explorer.Util.leftPad;
				return `${year}/${leftPad(month, 2, '0')}/${leftPad(day, 2, '0')} ${leftPad(hour, 2, '0')}:${leftPad(minute, 2, '0')}`;
			}
			static fileTypeLabel(extension, isFolder = false) {
				if (isFolder) {
					return $.explorer.filetypes["folder"];
				}

				var label = $.explorer.filetypes[extension];
				return (label) ? label : $.explorer.filetypes[""];
//				return $.explorer.filetypes[(!extension) ? "" : extension];
			}
			static displaySize(size) {
				var unit = "KB";
				var calced = Math.ceil(size / 1024);
				if (calced > 1024) {
					calced = Math.ceil(calced / 1024);
					unit = "MB";
				}
				if (calced > 1024) {
					calced = Math.ceil(calced / 1024);
					unit = "GB";
				}
				return `${calced} ${unit}`;
			}

		};

		return Util;
	})(),

	FileObject: (() => {
		function devide(path) {
			return path.split("/")
					   .filter(function(value, index, array){
						    return (value.length > 0);
					    });
		}

	    class FileObject {
			constructor(path, owner, size = 0, lastModifiedAt = new Date(),
				        fileObjects = null) {

				if (toString.call(path) !== "[object String]" || path.trim() === "") {
					throw "path must not be empty, and String.";
				}
				if (toString.call(owner) !== "[object String]" || owner.trim() === "") {
					throw "owner must not be empty, and String.";
				}
				if (toString.call(size) !== "[object Number]" || size < 0) {
					throw "size must be Number, and not be negative.";
				}
				if (toString.call(lastModifiedAt) !== "[object Date]") {
					throw "lastModifiedAt must be Date.";
				}
				if (fileObjects && !(fileObjects instanceof $.explorer.FileObjects)) {
					throw "fileObjects must be $.explorer.FileObjects.";
				}

				this._path = path;
				this._owner = owner;
				this._size = size;
				this._lastModifiedAt = lastModifiedAt;

				if (!this.isFolder && fileObjects) {
					throw "file cannot has fileObjects.";
				}

				if (this.isFolder) {
					this._fileObjects = fileObjects ? fileObjects : new $.explorer.FileObjects();
				}

				this._branches = devide(path);
			}
			get path() {
				return this._path;
			}
			get size() {
				return this._size;
			}
			get lastModifiedAt() {
				return this._lastModifiedAt;
			}
			get isFile() {
				return !this.isFolder;
			}
			get isFolder() {
				return this.path.endsWith('/');
			}
			get extension() {
				if (this.isFolder) return "";

				let index = this.name.lastIndexOf(".");
				return (index < 0) ? "" : this.name.substring(index + 1);
			}
			get name() {
				return this.branches[this.branches.length - 1];
			}
			get branches() {
				return this._branches;
			}
			get fileObjects() {
				return this._fileObjects;
			}
			addFolder(foldername, owner) {
				if (!this.isFolder) {
					throw "this operation is unsupported.";
				}

				var fileObject = $.explorer.FileObject.of(this.path + foldername + "/", owner);
				this._fileObjects.add(fileObject);
			}
			addFile(filename, owner, size) {
				if (!this.isFolder) {
					throw "this operation is unsupported.";
				}
				var fileObject = $.explorer.FileObject.of(this.path + filename, owner, size);
				this._fileObjects.add(fileObject);
			}
			static of(path, owner) {
				return FileObject.of(path, owner, 0);
			}
			static of(path, owner, size) {
				return new FileObject(path, owner, size, new Date());
			}
			static of(path, owner, size, lastModifiedAt) {
				return new FileObject(path, owner, size, lastModifiedAt);
			}
			static of(path, owner, size, lastModifiedAt, fileObjects) {
				return new FileObject(path, owner, size, lastModifiedAt, fileObjects);
			}
	    };

		return FileObject;
	})(),

	FileObjects: (() => {
		class FileObjects extends Array {
			push(fileObject) {
				this.add(fileObject);
			}
			add(fileObject) {
				if (!(fileObject instanceof $.explorer.FileObject)) {
					throw "fileObject must be FileObject.";
				}
				super.push(fileObject);
			}
		};

		return FileObjects;
	})(),

	FileObjectView: (() => {
		function focusFolder(target, on = true) {
			var $target = $(target);
			if (!$target.hasClass("explorer-fileobjects-row explorer-fileobjects-folder")) {
				var candidates = $target.closest(".explorer-fileobjects-row.explorer-fileobjects-folder");
				if (candidates.length > 0) {
					$target = $(candidates[0]);
				} else {
					$target = null;
				}
			}
			if (!$target) {
				return;
			}

			if (on) {
				if (!$target.hasClass("explorer-fileobject-folder-selected")) {
					$target.addClass("explorer-fileobject-folder-selected");
				}
			} else {
				if ($target.hasClass("explorer-fileobject-folder-selected")) {
					$target.removeClass("explorer-fileobject-folder-selected");
				}
			}
		};
		var eventHandler = {
			dragenter: function(event) {
				focusFolder(event.currentTarget, true);
			},
			dragleave: function(event) {
				focusFolder(event.currentTarget, false);
			},
			drop: function(event) {
				focusFolder(event.currentTarget, false);

				event.stopPropagation();
				event.preventDefault();

				var $currentTarget = $(event.currentTarget);
				var dropInFolderEvent = $.Event("dropInFolder.explorer")
				$currentTarget.trigger($.extend(event, dropInFolderEvent), ($currentTarget.data('view') || {}));
			},
			dblclickFile: function(event) {
				var $currentTarget = $(event.currentTarget);
				var openFileEvent = $.Event("openFile.explorer")
				$currentTarget.trigger($.extend(event, openFileEvent), ($currentTarget.data('view') || {}));
			},
			dblclickFolder: function(event) {
				var $currentTarget = $(event.currentTarget);
				var openFolderEvent = $.Event("openFolder.explorer")
				$currentTarget.trigger($.extend(event, openFolderEvent), ($currentTarget.data('view') || {}));
			}
		};
		class FileObjectView {
			constructor(options = {}) {
			    var settings = $.extend({el: "<td/>"}, options);
			    this._settings = settings;
				this._el = $(settings.el);
				this._model = settings.model;
			}
			get el() {
				return this._el;
			}
			get model() {
				return this._model;
			}
			render() {
				var $el = this.el;
				$el.addClass("explorer-fileobjects-row");
				var iconStyleClass = "explorer-fileobjects-file-icon";
				if (this.model.isFolder) {
					iconStyleClass = "explorer-fileobjects-folder-icon";
				} else {
					if (this.model.extension === "txt") {
						iconStyleClass = "explorer-fileobjects-txt-icon";
					} else if (this.model.extension === "png") {
						iconStyleClass = "explorer-fileobjects-png-icon";
					}
				}
				var $filename = $("<td/>").addClass("explorer-fileobjects-cell explorer-fileobjects-flename")
									.append($("<span/>")
									.addClass(iconStyleClass)
									.text(this.model.name));
				var $lastModifiedAt = $("<td/>").addClass("explorer-fileobjects-cell explorer-fileobjects-lastModifiedAt")
										.text($.explorer.Util.formatDate(this.model.lastModifiedAt));
				var $filetype = $("<td/>").addClass("explorer-fileobjects-cell explorer-fileobjects-filetype")
									.text($.explorer.Util.fileTypeLabel(this.model.extension, this.model.isFolder));
				var $filesize = $("<td/>").addClass("explorer-fileobjects-cell explorer-fileobjects-filesize")
								.text($.explorer.Util.displaySize(this.model.size));
				$el.append($filename)
				   .append($lastModifiedAt)
				   .append($filetype)
			       .append($filesize);

		    	$el.data("model", {path: this.model.path});
				$el.data("view", this);

			    if (this.model.isFolder) {
			    	$el.addClass("explorer-fileobjects-folder");

			    	$el.on("dragenter.explorer.folder", eventHandler.dragenter);
			    	$el.on("dragleave.explorer.folder", eventHandler.dragleave);
			    	$el.on("drop.explorer.folder", eventHandler.drop);
			    	$el.on("dblclick.explorer.folder", eventHandler.dblclickFolder);
			    } else {
			    	$el.on("dblclick.explorer.file", eventHandler.dblclickFile);
			    }

			    return $el;
			}
		};

		return FileObjectView;
	})(),

	FileObjectsView: (() => {
		function collectionSort(collection) {
	        var clone = $.extend(true, [], collection);
	        var compareTo = function(a, b) {
	          if (a < b) {
	            return -1;
	          }
	          if (a > b) {
	            return 1;
	          }
	          return 0;
	        };
	        return clone.sort(function (a, b) {
	          if (a.isFolder) {
	            if (b.isFolder) {
	              return compareTo(a.name, b.name);
	            } else {
	              return -1;
	            }
	          } else {
	            if (b.isFolder) {
	              return 1;
	            } else {
	              return compareTo(a.name, b.name);
	            }
	          }
	        });
		}
		class FileObjectsView {
			constructor(options = {}) {
			    var settings = $.extend({el: "<table/>", collection: []}, options);
			    this._settings = settings;
				this._el = $(settings.el);
				this._collection = settings.collection;
			}
			get el() {
				return this._el;
			}
			get collection() {
				return this._collection;
			}
			render() {
				var $el = this.el;
				$el.addClass("explorer-fileobjects");
				var $thead = $("<thead/>").addClass("header")
								.append(
									$("<tr/>").addClass("row")
										.append($("<th/>").addClass("explorer-fileobjects-header-cell explorer-fileobjects-filename").text("名前"))
										.append($("<th/>").addClass("explorer-fileobjects-header-cell explorer-fileobjects-lastModifiedAt").text("更新日時"))
										.append($("<th/>").addClass("explorer-fileobjects-header-cell explorer-fileobjects-filetype").text("種類"))
										.append($("<th/>").addClass("explorer-fileobjects-header-cell explorer-fileobjects-filesize").text("サイズ"))
								);
				var $tbody = $("<tbody/>");
				var fileObjects = this.collection;
				collectionSort(fileObjects).forEach(function(fileObject, index){
					var fileObjectView = new $.explorer.FileObjectView({model: fileObject});
					$tbody.append(fileObjectView.render());
				});

				$el.append($thead)
				   .append($tbody);

				$el.data("view", this);

				return $el;
			}
			update() {
				var $el = this.el;
				var $tbody = $el.find("tbody");
				$tbody.empty();
				var fileObjects = this.collection;
				collectionSort(fileObjects).forEach(function(fileObject, index){
					var fileObjectView = new $.explorer.FileObjectView({model: fileObject});
					$tbody.append(fileObjectView.render());
				});

				return $el;
			}
		};

		return FileObjectsView;
	})(),

	ContextMenuView: (() => {
		class ContextMenuView {
			constructor() {
				this._el = $("<div/>");
			}
			render() {
				var $el = this._el;
				$el.addClass("explorer-contextmenu diactive");
				var menus = $("<ul/>").append(
					$("<li/>")
						.text("Create folder...")
						.on("click.explorer.contextmenu", function(event){
							var folder = prompt("Input new folder name.");
							console.log(folder);
						})
				);
				$el.append(menus);

				var $this = this;
				$el.on("click.explorer.contextmenu", function(event){
					$this.deactive();
				});

				return $el;
			}
			active(position = {}) {
				var $el = this._el;
				$el.css({top: position.top, left: position.left})
				   .addClass("active")
				   .removeClass("diactive");
				return $el;
			}
			deactive() {
				var $el = this._el;
				$el.addClass("diactive").removeClass("active");
				return $el;
			}
		};

		return ContextMenuView;
	})(),

	BlankZoneView: (() => {
	    class BlankZoneView {
	    	constructor() {
	    		this._el = $("<div/>");
	    	}
	    	get el() {
	    		return this._el;
	    	}
	    	render() {
	    		var $el = this.el;

	    		// auto width and height setting...as available
	    		$el.css("height", "100%"); // fixme temprary

	    		var contextMenuView = new $.explorer.ContextMenuView();
	    		$el.append(contextMenuView.render());
	    		$el.on("contextmenu.explorer", function(event) {
	    			contextMenuView.active({top: event.pageY, left: event.pageX});

	    			event.preventDefault();
	    			event.stopPropagation();
	    		});
	    		$el.on("click.explorer", function(event){
	    			contextMenuView.deactive();
	    		});

	    		return $el;
	    	}
	    };

		return BlankZoneView;		
	})(),

	ExplorerView: (() => {
		var functions = {
			createFolder: function(folder) {
				console.log("ExplorerView.functions.createFolder");
				console.log(folder);
			},
			dropFiles: function(files = [], folder, fileObject) {
				console.log("ExplorerView.functions.dropFiles");
				console.log(files);
				console.log(folder);

				if (fileObject) {
					$(files).each(function(){
						fileObject.addFile(this.name, "owner", this.size);
					});
				}
			},
			openFile: function(file, fileObject) {
				console.log("ExplorerView.functions.openFile");
				console.log(file);
			},
			openFolder: function(folder, fileObject) {
				console.log("ExplorerView.functions.openFolder");
				console.log(folder);
			}
		};
		var eventHandler = {
			dragenter: function(event) {
			},
			dragleave: function(event) {
			},
			dragover: function(event) {
				event.stopPropagation();
				event.preventDefault();

				event.originalEvent.dataTransfer.dropEffect = 'copy';
			},
			drop: function(event) {
				console.log(event);
				event.stopPropagation();
				event.preventDefault();

				let files = event.originalEvent.dataTransfer.files;
				for (var i = 0, f; f = files[i]; i++) {
					let tostring = `file[${i}]: {type: ${f.type}, name: ${f.name}, size: ${f.size}}`;
					console.log(tostring);
				}

				var view = $(event.currentTarget).data("view");
				var fileObject = view._settings.fileObject;
				var callbackDropFiles = view._settings.callbackDropFiles;
				callbackDropFiles(files, fileObject.path, fileObject);
				view._fileObjectsView.update();
			},
			dropInFolder: function(event, data = {}) {
				console.log(event);
				console.log(data);

				let files = event.originalEvent.dataTransfer.files;

				var view = $(event.currentTarget).data("view");
				var fileObject = view._settings.fileObject;
				var callbackDropFiles = view._settings.callbackDropFiles;
				callbackDropFiles(files, data._model.path, data._model);

				event.stopPropagation();
				event.preventDefault;
			},
			openFile: function(event, data = {}) {
				var callback = $(event.currentTarget).data("view")._settings.callbackOpenFile;
				callback(data._model.path, data._model);
			},
			openFolder: function(event, data = {}) {
				var callback = $(event.currentTarget).data("view")._settings.callbackOpenFolder;
				callback(data._model.path, data._model);
			}
		}
		class ExplorerView {
			constructor(options = {}) {
			    var settings = $.extend({
			    	el: "<div/>",
			    	callbackCreateFolder: functions.createFolder,
			    	callbackDropFiles:    functions.dropFiles,
			    	callbackOpenFile:     functions.openFile,
			    	callbackOpenFolder:   functions.openFolder
				}, options);

			    this._settings = settings;
				this._el = $(settings.el);
			}
			get el() {
				return this._el;
			}
			render() {
				var $el = this.el;

				$el.addClass("explorer");
				var fileObject = this._settings.fileObject;
				var fileObjects = fileObject.fileObjects;
				var fileObjectsView = new $.explorer.FileObjectsView({collection: fileObjects});
				this._fileObjectsView = fileObjectsView;
				var blankZoneView = new $.explorer.BlankZoneView();

				$el.append(fileObjectsView.render());
				$el.append(blankZoneView.render());
//				explorer.append(contextmenu); // TODO add context-menu

				$el.on("drop.explorer", eventHandler.drop);
				$el.on("dragenter.explorer", eventHandler.dragenter);
				$el.on("dragleave.explorer", eventHandler.dragleave);
				$el.on("dragover.explorer", eventHandler.dragover);
				$el.on("dropInFolder.explorer", eventHandler.dropInFolder);
				$el.on("openFile.explorer", eventHandler.openFile);
				$el.on("openFolder.explorer", eventHandler.openFolder);

				$el.data("view", this);

				return $el;
			}
		};

		return ExplorerView;
	})(),
});

(function($){

	var defaults = {
		fileObject: $.explorer.FileObject.of("/", "root")
	};
	var methods = {
		init: function(options) {
			return this.each(function() {
				var $this = $(this),
				    data = $this.data('explorer'),
				    settings = $.extend(defaults, options);
				if (!data) {

					$this.data('explorer', {
						target: $this,
						settings: settings
					});

					var explorerView = new $.explorer.ExplorerView($.extend({el: $this}, settings));
					explorerView.render();
				}
			});
		},
		destroy: function() {
			return this.each(function() {
				var $this = $(this),
				    data = $this.data('explorer');

				$this.off('.explorer');
				data.explorer.remove();
				$this.removeData('explorer');
			});
		}
	};

	$.fn.explorer = function(method) {
		if (methods[method]) {
			console.log(`explorer method exist`);
			console.log(arguments);
			return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
		} else if (typeof method === 'object' || !method) {
			return methods.init.apply(this, arguments);
		} else {
			$.error('Method ' + method + ' does not exist on jQuery.tooltip')
		}
	}
})(jQuery);
