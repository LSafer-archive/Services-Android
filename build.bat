call rmdir /s /q Services\build
call gradle build
call copy Services\build\outputs\aar\Services-release.aar \Aunionx\Libraries\LSaferSE\Services.aar