const processFile = require("../middleware/upload");
const { format } = require("util");
const { Storage } = require("@google-cloud/storage");

const storage = new Storage({ keyFilename: "google-cloud-key.json" });
const bucket = storage.bucket("angelo-bucket");

const upload = async (req, res) => {
  try {
    await processFile(req, res);

    if (!req.file) {
      return res.status(400).send({ message: "Silakan upload file!" });
    }

    const blob = bucket.file(req.file.originalname);
    const blobStream = blob.createWriteStream({
      resumable: false,
    });

    blobStream.on("error", (err) => {
      res.status(500).send({ message: err.message });
    });

    blobStream.on("finish", async (data) => {
      const publicUrl = format(
        `https://storage.googleapis.com/${bucket.name}/${blob.name}`
      );

      try {
        await bucket.file(req.file.originalname).makePublic();
      } catch {
        return res.status(500).send({
          message: `Uploaded the file successfully: ${req.file.originalname}, but public access is denied!`,
          url: publicUrl,
        });
      }

      res.status(200).send({
        message: "Upload file berhasil: " + req.file.originalname,
        url: publicUrl,
      });
    });

    blobStream.end(req.file.buffer);
  } catch (err) {
    console.log(err);

    if (err.code == "LIMIT_FILE_SIZE") {
      return res.status(500).send({
        message: "Ukuran File tidak boleh lebih dari 1000MB!",
      });
    }

    res.status(500).send({
      message: `Tidak dapat mengupload file: ${req.file.originalname}. ${err}`,
    });
  }
};

const getListFiles = async (req, res) => {
  try {
    const [files] = await bucket.getFiles();
    let fileInfos = [];

    files.forEach((file) => {
      fileInfos.push({
        name: file.name,
        url: file.metadata.mediaLink,
      });
    });

    res.status(200).send(fileInfos);
  } catch (err) {
    console.log(err);

    res.status(500).send({
      message: "Tidak dapat membaca list files!",
    });
  }
};

const download = async (req, res) => {
  try {
    const [metaData] = await bucket.file(req.params.name).getMetadata();
    res.redirect(metaData.mediaLink);
  } catch (err) {
    res.status(500).send({
      message: "Tidak dapat mendownload file. " + err,
    });
  }
};

module.exports = {
  upload,
  getListFiles,
  download,
};
