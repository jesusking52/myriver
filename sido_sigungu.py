#-*- coding: utf-8 -*-
import json
import io
import openpyxl
file_path = '20160406_zip_code_sido_sigungu.xlsx'
workbook = openpyxl.load_workbook(file_path, use_iterators=True)
sheet_names = workbook.get_sheet_names()

sheet = workbook.get_sheet_by_name(name=sheet_names[0])
idx = 0

result = {}

for row in sheet.rows:
	if idx == 0:
		idx += 1
		continue
	sido = row[1].value
	sigungu = row[2].value
	
	sigungu_array = result.get(sido)
	if sigungu_array == None:
		result[sido] = [sigungu]
	else:
		if sigungu in sigungu_array:
			continue
		else:
			sigungu_array.append(sigungu)

print result
with open('sido_sigungu.json', 'w') as fp:
	fianl_result = json.dumps(result, ensure_ascii=False).encode('utf8')
	fp.write(fianl_result)


