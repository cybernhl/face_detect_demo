package com.guadou.lib_baselib.ext


/**
 * 哈希，加密相关
 */
fun String.md5() = EncryptUtils.encryptMD5ToString(this)

fun String.sha1() = EncryptUtils.encryptSHA1ToString(this)

fun String.sha256() = EncryptUtils.encryptSHA256ToString(this)

fun String.sha512() = EncryptUtils.encryptSHA512ToString(this)

/**
 * 随机数增强的md5算法
 * @param salt 加盐的值
 */
fun String.md5Hmac(salt: String) = EncryptUtils.encryptHmacMD5ToString(this, salt)

fun String.sha1Hmac(salt: String) = EncryptUtils.encryptHmacSHA1ToString(this, salt)

fun String.sha256Hmac(salt: String) = EncryptUtils.encryptHmacSHA256ToString(this, salt)

/**
 * DES对称加密
 * @param key 长度必须是8位
 */
fun String.encryptDES(key: String) = EncryptUtils.encryptDES(this, key)

/**
 * DES对称解密
 * @param key 长度必须是8位
 */
fun String.decryptDES(key: String) = EncryptUtils.decryptDES(this, key)

/**
 * AES对称加密
 * @param key 长度必须是16位
 */
fun String.encryptAES(key: String) = EncryptUtils.encryptAES(this, key)

/**
 * AES对称解密
 * @param key 长度必须是16位
 */
fun String.decryptAES(key: String) = EncryptUtils.decryptAES(this, key)

/**
 * Base64编码
 * @param str  原始数据
 */
fun String.encodeBase64(str: String) = EncryptUtils.encodeStrByBase64(str)

/**
 * Base64解码
 * @param str Base64数据
 */
fun String.decodeBase64(str: String) = EncryptUtils.deCodeStrByBase64(str)