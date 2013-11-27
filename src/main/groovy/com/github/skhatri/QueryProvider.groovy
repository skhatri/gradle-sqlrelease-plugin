package com.github.skhatri


enum QueryProvider {

    SYBASE('BEGIN\n' +
            '        declare @maxid int\n' +
            '        select @maxid = (select coalesce(max(id), 0)+1 from version_info)\n' +
            '        insert into version_info(id, version, filename, app, starttime) values(@maxid, ?, ?, ?, getdate())\n' +
            '        END\n', 'update version_info set endtime = getdate() where app = ? and version = ? and id = (select max(id) from version_info where version=?)'),
    MYSQL('insert into version_info(version, filename, app, starttime) values(?, ?, ?, now())',
            'update version_info v1 set v1.endtime = now() where v1.id in (select id from ( select max(id) as id from version_info where app = ? and version = ? ) as tmp_table)')

    private String start
    private String update
    private QueryProvider(start, update){
        this.start = start
        this.update = update
    }
    public String getStartSQL() {
        return start
    }

    public String getUpdateSQL() {
        return update
    }
    public String getVersionSQL() {
        return 'select max(version) as version from version_info where app = ? and endtime is not null'
    }
}
