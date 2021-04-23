# -*- coding: utf-8 -*-

# Form implementation generated from reading ui file 'MainMenu.ui'
#
# Created by: PyQt5 UI code generator 5.13.0
#
# WARNING! All changes made in this file will be lost!


from PyQt5 import QtCore, QtGui, QtWidgets


class Ui_MainWindow(object):
    def setupUi(self, MainWindow):
        MainWindow.setObjectName("MainWindow")
        MainWindow.resize(967, 759)
        MainWindow.setAcceptDrops(False)
        MainWindow.setAutoFillBackground(False)
        self.centralwidget = QtWidgets.QWidget(MainWindow)
        self.centralwidget.setObjectName("centralwidget")
        self.DropSection = QtWidgets.QFrame(self.centralwidget)
        self.DropSection.setGeometry(QtCore.QRect(110, 140, 691, 351))
        self.DropSection.setFrameShape(QtWidgets.QFrame.StyledPanel)
        self.DropSection.setFrameShadow(QtWidgets.QFrame.Raised)
        self.DropSection.setObjectName("DropSection")
        self.verticalLayout = QtWidgets.QVBoxLayout(self.DropSection)
        self.verticalLayout.setObjectName("verticalLayout")
        self.widget = QtWidgets.QWidget(self.DropSection)
        self.widget.setAcceptDrops(True)
        self.widget.setAutoFillBackground(False)
        self.widget.setObjectName("widget")
        self.plainTextEdit = QtWidgets.QPlainTextEdit(self.widget)
        self.plainTextEdit.setGeometry(QtCore.QRect(180, 30, 311, 181))
        self.plainTextEdit.setAcceptDrops(False)
        self.plainTextEdit.setBackgroundVisible(True)
        self.plainTextEdit.setObjectName("plainTextEdit")
        self.FileTreeButton = QtWidgets.QPushButton(self.widget)
        self.FileTreeButton.setGeometry(QtCore.QRect(190, 222, 291, 41))
        self.FileTreeButton.setObjectName("FileTreeButton")
        self.verticalLayout.addWidget(self.widget)
        self.horizontalScrollBar = QtWidgets.QScrollBar(self.DropSection)
        self.horizontalScrollBar.setOrientation(QtCore.Qt.Horizontal)
        self.horizontalScrollBar.setObjectName("horizontalScrollBar")
        self.verticalLayout.addWidget(self.horizontalScrollBar)
        self.ButOption = QtWidgets.QFrame(self.centralwidget)
        self.ButOption.setGeometry(QtCore.QRect(110, 520, 681, 170))
        self.ButOption.setFrameShape(QtWidgets.QFrame.StyledPanel)
        self.ButOption.setFrameShadow(QtWidgets.QFrame.Raised)
        self.ButOption.setObjectName("ButOption")
        self.horizontalLayout = QtWidgets.QHBoxLayout(self.ButOption)
        self.horizontalLayout.setObjectName("horizontalLayout")
        self.Compress = QtWidgets.QPushButton(self.ButOption)
        self.Compress.setMinimumSize(QtCore.QSize(200, 150))
        font = QtGui.QFont()
        font.setPointSize(20)
        self.Compress.setFont(font)
        self.Compress.setObjectName("Compress")
        self.horizontalLayout.addWidget(self.Compress)
        spacerItem = QtWidgets.QSpacerItem(66, 20, QtWidgets.QSizePolicy.Expanding, QtWidgets.QSizePolicy.Minimum)
        self.horizontalLayout.addItem(spacerItem)
        self.Reset = QtWidgets.QPushButton(self.ButOption)
        self.Reset.setMinimumSize(QtCore.QSize(100, 50))
        font = QtGui.QFont()
        font.setPointSize(10)
        self.Reset.setFont(font)
        self.Reset.setObjectName("Reset")
        self.horizontalLayout.addWidget(self.Reset)
        spacerItem1 = QtWidgets.QSpacerItem(65, 20, QtWidgets.QSizePolicy.Expanding, QtWidgets.QSizePolicy.Minimum)
        self.horizontalLayout.addItem(spacerItem1)
        self.DeCompress = QtWidgets.QPushButton(self.ButOption)
        self.DeCompress.setMinimumSize(QtCore.QSize(200, 150))
        font = QtGui.QFont()
        font.setPointSize(20)
        self.DeCompress.setFont(font)
        self.DeCompress.setAutoFillBackground(False)
        self.DeCompress.setObjectName("DeCompress")
        self.horizontalLayout.addWidget(self.DeCompress)
        self.textEdit = QtWidgets.QTextEdit(self.centralwidget)
        self.textEdit.setGeometry(QtCore.QRect(130, 40, 631, 71))
        self.textEdit.setAutoFillBackground(True)
        self.textEdit.setObjectName("textEdit")
        self.treeView = QtWidgets.QTreeView(self.centralwidget)
        self.treeView.setEnabled(False)
        self.treeView.setGeometry(QtCore.QRect(0, 30, 191, 161))
        self.treeView.setFrameShadow(QtWidgets.QFrame.Sunken)
        self.treeView.setObjectName("treeView")
        MainWindow.setCentralWidget(self.centralwidget)
        self.statusbar = QtWidgets.QStatusBar(MainWindow)
        self.statusbar.setObjectName("statusbar")
        MainWindow.setStatusBar(self.statusbar)

        self.retranslateUi(MainWindow)
        self.FileTreeButton.clicked.connect(self.treeView.reset)
        self.FileTreeButton.pressed.connect(self.treeView.show)
        self.DeCompress.clicked.connect(MainWindow.repaint)
        self.Reset.clicked.connect(self.widget.repaint)
        self.Compress.pressed.connect(MainWindow.update)
        QtCore.QMetaObject.connectSlotsByName(MainWindow)

    def retranslateUi(self, MainWindow):
        _translate = QtCore.QCoreApplication.translate
        MainWindow.setWindowTitle(_translate("MainWindow", "File Incremental Backup"))
        self.plainTextEdit.setPlainText(_translate("MainWindow", "Drop Box or Press button to bring up the menu for selecting files."))
        self.FileTreeButton.setText(_translate("MainWindow", "Select Files"))
        self.Compress.setText(_translate("MainWindow", "Compress"))
        self.Reset.setText(_translate("MainWindow", "Reset"))
        self.DeCompress.setText(_translate("MainWindow", "DeCompress"))
        self.textEdit.setHtml(_translate("MainWindow", "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0//EN\" \"http://www.w3.org/TR/REC-html40/strict.dtd\">\n"
"<html><head><meta name=\"qrichtext\" content=\"1\" /><style type=\"text/css\">\n"
"p, li { white-space: pre-wrap; }\n"
"</style></head><body style=\" font-family:\'MS Shell Dlg 2\'; font-size:8.25pt; font-weight:400; font-style:normal;\">\n"
"<p align=\"center\" style=\" margin-top:0px; margin-bottom:0px; margin-left:0px; margin-right:0px; -qt-block-indent:0; text-indent:0px;\"><span style=\" font-size:10pt;\">Welcome to My File Compression Algorithm. There is 2 functions to be done. Who really cares as it doesn\'t work right now but that is ok.</span></p></body></html>"))
