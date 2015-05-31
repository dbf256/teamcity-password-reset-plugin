<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
  <head>
    <title>Password reset &mdash; TeamCity</title>
    <style type="text/css">
        body {
            background-color: #afb6bd;
            margin: 0;
            padding: 0;
            font: 82%/1.5em "Helvetica Neue",Arial,sans-serif;
        }

        .rp_password_reset_div {
            background: #e3e9ef;
            border: 1em solid #fff;
            margin: 0 auto;
            padding: 0 0 1em;
            position: relative;
            text-align: left;
            top: 150px;
            width: 600px;
        }

        .rp_content_div {
            margin: 20px;
        }

        .rp_button {
            background-color: #e6e6e6;
            background-image: linear-gradient(#ffffff, #ffffff 25%, #e6e6e6);
            background-repeat: no-repeat;
            border-color: #ccc #ccc #bbb;
            border-image: none;
            border-radius: 4px;
            border-style: solid;
            border-width: 1px;
            box-shadow: 0 1px 0 rgba(255, 255, 255, 0.2) inset, 0 1px 2px rgba(0, 0, 0, 0.05);
            color: #333;
            cursor: pointer;
            display: inline-block;
            font-size: 13px;
            line-height: normal;
            outline: 0 none;
            padding: 5px 14px 6px;
            text-shadow: 0 1px 1px rgba(255, 255, 255, 0.75);
        }

        .rp_text {
            font-size: 14px;
            padding: 1px 0;
            width: 240px;
            border-color: #888 #ccc #ccc;
            border-image: none;
            border-style: solid;
            border-width: 1px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1) inset;
            font-size: 13px;
            line-height: 1.5em;
            margin: 0;
            min-height: 20px !important;
            padding: 1px 3px;
        }

        .rp_label {
            font-size: 18px;
            white-space: nowrap;
            font-weight: normal;
            text-align: right;
        }

        .rp_h1 {
            border-bottom: 1px solid #babec1;
            font-family: trebuchet ms,verdana,tahoma,arial,sans-serif;
            font-size: 220%;
            font-weight: normal;
            margin: 0;
            padding: 0 0 0.4em;
        }

        .rp_table {
            margin-top: 20px;
            margin-bottom: 10px;
        }

        .rp_table tr {
            margin: 5px;
        }

        .rp_error {
            color: #ed2c10;
            font-weight: bold;
        }

        a {
            color: #1564c2;
            text-decoration: none;
        }

        .rp_login_link {
            font-size: 90%;
            color: #1564c2;
        }

    </style>
  </head>
  <body>
