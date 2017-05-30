from sqlalchemy import create_engine


e = create_engine('sqlite:///database/wim_info_first.db')

def get_switch(segment):
	conn = e.connect()
	query = conn.execute('SELECT port_id, bridge_name FROM connectivity WHERE segment="%s";'%segment)
	dt = query.fetchone()
	port, switch = dt[0],dt[1]
	print port, switch
	return port, switch

get_switch("10.100.32.200/24")