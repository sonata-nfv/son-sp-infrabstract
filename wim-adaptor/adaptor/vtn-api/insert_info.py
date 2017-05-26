from sqlalchemy import create_engine 
from sqlalchemy.orm import sessionmaker

from sqlalchemy_declaritive import Base, Connectivity

engine = create_engine('sqlite:///wim_info_first.db')
# bind the engine to the metadata of the base 
Base.metadata.bind = engine 
DBSession = sessionmaker(bind=engine)
# dbsession establishes 'conversations' with the database 
session = DBSession()
#insert data on the table(s)
new_conn = Connectivity(segment='10.100.32.200/24', bridge_name='vb4',port_id='if6',location='Athens')
session.add(new_conn)
session.commit()


