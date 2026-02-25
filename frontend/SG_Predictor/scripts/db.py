import psycopg2

connection = psycopg2.connect(
    host="db.psyqxrrjfmrelslqodpu.supabase.co",
    database="postgres",
    user="postgres",
    password="!Ssalssal7777",
    port="5432"
)

cursor = connection.cursor()