rem 切换分支
git checkout master
rem 合并分支
git merge dev
rem 推送到主分支
git push gitee master
rem 合并 github master 分支
git fetch github  master:master
rem 推送到开发分支
git push github dev
rem  推送到主分支
git push github master
rem 切换到开发分支
git checkout dev