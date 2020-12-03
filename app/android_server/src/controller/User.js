import moment from 'moment';
import { v4 as uuidv4 } from 'uuid';
import db from '../db';
import Helper from './Helper';

const User = {
  /**
   * Create A User
   * @param {object} req 
   * @param {object} res
   * @returns {object} reflection object 
   */
  async create(req, res) {
    if (!req.body.login || !req.body.password) {
      return res.status(400).send({'message': 'Some values are missing'});
    }
    if (!Helper.isValidLogin(req.body.login)) {
      return res.status(400).send({ 'message': 'Please enter a valid login' });
    }
    const hashPassword = Helper.hashPassword(req.body.password);

    const createQuery = `INSERT INTO
      users(id, login, password,level)
      VALUES($1, $2, $3, $4)
      returning *`;
	 
    const values = [
      uuidv4(),
      req.body.login,
      hashPassword,
      0
    ];

    try {
      const { rows } = await db.query(createQuery, values);
      const token = Helper.generateToken(rows[0].id);
      return res.status(201).send({ 'message': 'User created successfully', token });
    } catch(error) {
      if (error.routine === '_bt_check_unique') {
        return res.status(400).send({ 'message': 'User with that login already exist' })
      }
      return res.status(400).send(error);
    }
  },
  
async chagePass(req, res) {
	 if (!req.params.login || !req.body.password) {
      return res.status(400).send({'message': 'Some values are missing'});
    }
	const text = 'SELECT * FROM users WHERE login = $1';
    try {
      const { rows } = await db.query(text, [req.params.login]);
      if(!rows[0]) {
        return res.status(404).send({'message': 'user not found'});
      }
       if(!Helper.comparePassword(rows[0].password, req.body.password)) {
        return res.status(400).send({ 'message': 'The credentials you provided is incorrect' });
      }
	   const hashPassword = Helper.hashPassword(req.body.newPassword);
	  const values = [
	hashPassword,
	req.params.login
    ];
	  const createQuery = 'UPDATE users SET password = $1 WHERE login = $2';
      const { rowsRes } = await db.query(createQuery, values);
      return res.status(404).send({'message': 'Success'});
	} catch(error) {
      return res.status(400).send(error);
    }

  },
  
async getUser(req, res) {
	const text = 'SELECT * FROM users WHERE login = $1';
    try {
      const { rows } = await db.query(text, [req.params.login]);
      if(!rows[0]) {
        return res.status(404).send({'message': 'user not found'});
      }
        return res.json(rows[0]);
    } catch(error) {
      return res.status(400).send(error);
    }
  },
  /**
   * Login
   * @param {object} req 
   * @param {object} res
   * @returns {object} user object 
   */
  async login(req, res) {
    if (!req.body.login || !req.body.password) {
      return res.status(400).send({'message': 'Some values are missing'});
    }
    if (!Helper.isValidLogin(req.body.login)) {
      return res.status(400).send({ 'message': 'Please enter a valid login address' });
    }
    const text = 'SELECT * FROM users WHERE login = $1';
    try {
      const { rows } = await db.query(text, [req.body.login]);
      if (!rows[0]) {
        return res.status(400).send({'message': 'The credentials you provided is incorrect'});
      }
      if(!Helper.comparePassword(rows[0].password, req.body.password)) {
        return res.status(400).send({ 'message': 'The credentials you provided is incorrect' });
      }
      const token = Helper.generateToken(rows[0].id);
	  const message = req.body.login;
      return res.status(200).send({ message, token });
    } catch(error) {
      return res.status(400).send(error)
    }
  },
  /**
   * Delete A User
   * @param {object} req 
   * @param {object} res 
   * @returns {void} return status code 204 
   */
  async delete(req, res) {
    const deleteQuery = 'DELETE FROM users WHERE login=$1 returning *';
    try {
      const { rows } = await db.query(deleteQuery, [req.body.login]);
      if(!rows[0]) {
        return res.status(404).send({'message': 'user not found'});
      }
      return res.status(204).send({ 'message': 'deleted' });
    } catch(error) {
      return res.status(400).send(error);
    }
  }
}

export default User;