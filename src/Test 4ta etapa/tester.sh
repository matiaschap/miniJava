#! /bin/bash
#! Matias Marzullo
FAILURE=1
SUCCESS=0

CURRENT_DIR=`pwd`

for file in `ls`; do
	if [ "$file" != "Semantico.jar" ] && [ "$file" != "tester.sh" ]; then
		echo "${CURRENT_DIR}/b/$file"
		java -jar Semantico.jar "${CURRENT_DIR}/$file"
	fi
done

if [ $? -ne 0 ]
then
	echo "Tests failed"
	exit ${FAILURE}
fi

echo "Tests succeded"
exit ${SUCCESS}